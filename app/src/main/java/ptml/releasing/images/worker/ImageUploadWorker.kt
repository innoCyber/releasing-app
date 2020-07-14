package ptml.releasing.images.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ptml.releasing.R
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.data.Repository
import ptml.releasing.app.di.modules.worker.ChildWorkerFactory
import ptml.releasing.app.utils.Constants.UPLOAD_NOTIFICATION_CARGO_CODE
import ptml.releasing.app.utils.Constants.UPLOAD_NOTIFICATION_ID
import ptml.releasing.app.utils.ProgressRequestBody
import ptml.releasing.app.utils.upload.CancelWorkReceiver
import ptml.releasing.app.utils.upload.NotificationHelper
import ptml.releasing.app.utils.upload.NotificationHelper.Companion.SUMMARY_NOTIFICATION_ID
import ptml.releasing.device_configuration.view.DeviceConfigActivity
import ptml.releasing.images.api.ImageUploadRepository
import timber.log.Timber
import java.io.File


/**
Created by kryptkode on 8/6/2019
 */
class ImageUploadWorker @AssistedInject constructor(
    @VisibleForTesting val imageUploadRepository: ImageUploadRepository,
    @VisibleForTesting val repository: Repository,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val notificationHelper = NotificationHelper(context)
    private lateinit var notification: NotificationCompat.Builder
    private val imageBodies = mutableListOf<MultipartBody.Part>()
    private val fileNames = mutableListOf<String>()

    override suspend fun doWork(): Result {
        val cargoCode = inputData.getString(CARGO_CODE_KEY)
        val cargoId = inputData.getInt(CARGO_ID_KEY, 0)
        val operationStep = inputData.getInt(OPERATION_STEP_KEY, 0)
        val cargoType = inputData.getInt(CARGO_TYPE_KEY, 0)
        //begin upload for file images
        var result = Result.retry()

        cargoCode?.let { code ->
            var totalImages = 0
            Timber.d("Starting work")
            try {
                val images = repository.getImages(code)
                val imagesList = images.values.toList().filter {
                    it.isFile() && !it.uploaded
                }
                totalImages = imagesList.size
                for (i in 0 until totalImages) {
                    val image = imagesList[i]
                    val status = "${i + 1}/$totalImages"
                    fileNames.add(image.name ?: "image_${i}")
                    imageBodies.add(createMultipartBody(image.imageUri, cargoCode, status))
                }

                if (imageBodies.size > 0) {
                    val response =
                        uploadImage(cargoType, cargoId, operationStep, fileNames, imageBodies)
                    val success = response.isSuccess
                    Timber.d("Success: $success")
                    imagesList.forEach {
                        it.uploaded = success
                        repository.addImage(cargoCode, it)
                    }
                    result = if (success) Result.success() else Result.retry()
                }
            } catch (e: Throwable) {
                Timber.e(e)
                result = Result.retry()
            }


            if (result == Result.success()) {
                showSuccessNotification(cargoCode, totalImages)
            } else if (totalImages <= 0) {
                //NO images to be uploaded
                result = Result.success()
                Timber.e("Uploaded successfully")
            } else {
                Timber.e("Failed to upload")
                showFailureNotification(cargoCode)
            }

        }
        Timber.w("End of job")
        return result
    }

    private suspend fun uploadImage(
        cargoType: Int,
        cargoId: Int,
        operationStep: Int,
        fileNames: List<String>,
        files: List<MultipartBody.Part>
    ): BaseResponse {
        return imageUploadRepository.uploadImage(
            cargoType,
            cargoId,
            operationStep,
            fileNames,
            files
        )
    }

    /**
     * @param imageUri
     * @return multi part body
     */
    private fun createMultipartBody(
        imageUri: String?,
        cargoCode: String,
        status: String
    ): MultipartBody.Part {
        val fileUri = Uri.parse(imageUri)
        val file = File(fileUri.path ?: "")
        Timber.d("createMultipartBody")
        return MultipartBody.Part.createFormData(
            "UploadedImage",
            file.name,
            createCountingRequestBody(file, fileUri, cargoCode, status)
        )
    }

    private fun createCountingRequestBody(
        file: File,
        fileUri: Uri,
        cargoCode: String,
        status: String
    ): RequestBody {
        val requestBody = createRequestBodyFromFile(
            file,
            context.contentResolver.getType(fileUri) ?: ""
        )

        Timber.d("createCountingRequestBody")
        return ProgressRequestBody(requestBody,
            ProgressRequestBody.Listener { bytesWritten, contentLength ->
                val progress = 1.0 * bytesWritten / contentLength
                Timber.w("Progress: $progress NAME: ${file.name}")
                showProgressNotification(progress, file.name, cargoCode, status)
            })
    }

    private fun showProgressNotification(
        progress: Double,
        name: String,
        cargoCode: String,
        status: String
    ) {
        notification = notificationHelper.getProgressNotification(
            context.getString(R.string.uploading, status),
            context.getString(R.string.in_progress),
            (100 * progress).toInt()
        )
        notificationHelper.notify(cargoCode.hashCode(), notification)
        notificationHelper.notify(
            SUMMARY_NOTIFICATION_ID,
            notificationHelper.getSummaryNotification(
                context.getString(R.string.upload_in_progress),
                context.getString(R.string.upload_in_progress_msg)
            )
        )
//        localBroadcastManager.sendBroadcast(progressIntent)
    }

    private fun createRequestBodyFromFile(file: File, mimeType: String): RequestBody {
        Timber.d("createRequestBodyFromFile")
        return RequestBody.create(MediaType.parse(mimeType), file)
    }

    /**
     * Send Broadcast to FileProgressReceiver while file upload successful
     */
    private fun showSuccessNotification(cargoCode: String?, totalImages: Int) {
        val resultIntent = Intent(context, DeviceConfigActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
            context,
            0 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notification = notificationHelper.getNotification(
            context.getString(R.string.message_upload_success),
            context.getString(R.string.file_upload_successful, totalImages),
            resultPendingIntent
        )
        notificationHelper.notify(cargoCode.hashCode(), notification)
        notificationHelper.notify(
            SUMMARY_NOTIFICATION_ID,
            notificationHelper.getSummaryNotification(
                context.getString(R.string.upload_summary_title),
                context.getString(R.string.upload_summary_body)
            )
        )
    }

    private fun showFailureNotification(cargoCode: String?) {
        val activityIntent = Intent(context, DeviceConfigActivity::class.java)

        val resultPendingIntent = PendingIntent.getActivity(
            context,
            0 /* Request code */, activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val clearIntent = Intent(context, CancelWorkReceiver::class.java)
        clearIntent.action = CancelWorkReceiver.ACTION_CANCEL

        clearIntent.putExtra(UPLOAD_NOTIFICATION_ID, cargoCode.hashCode())
        clearIntent.putExtra(UPLOAD_NOTIFICATION_CARGO_CODE, cargoCode)

        val clearPendingIntent = PendingIntent.getBroadcast(
            context,
            0 /* Request code */, clearIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        notification = notificationHelper.getNotification(
            context.getString(R.string.message_upload_failed),
            context.getString(R.string.file_upload_failed_msg),
            resultPendingIntent
        )

        notification.addAction(
            android.R.drawable.ic_menu_revert,
            context.getString(R.string.cancel_action_text), clearPendingIntent
        )
        notificationHelper.notify(cargoCode.hashCode(), notification)
        notificationHelper.notify(
            SUMMARY_NOTIFICATION_ID,
            notificationHelper.getSummaryNotification(
                context.getString(R.string.upload_summary_title),
                context.getString(R.string.upload_summary_body)
            )
        )
    }


    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    companion object {

        const val CARGO_CODE_KEY = "cargo_code_key"
        const val CARGO_ID_KEY = "cargo_id_key"
        const val OPERATION_STEP_KEY = "operation_step"
        const val CARGO_TYPE_KEY = "cargo_type_key"

        fun createWorkRequest(
            cargoCode: String,
            operationStep: Int,
            cargoId: Int,
            cargoType: Int
        ): OneTimeWorkRequest {

            val myConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val inputData = Data.Builder()
                .putInt(CARGO_ID_KEY, cargoId)
                .putInt(CARGO_TYPE_KEY, cargoType)
                .putString(CARGO_CODE_KEY, cargoCode)
                .putInt(OPERATION_STEP_KEY, operationStep)
                .build()

            val workRequest = OneTimeWorkRequest
                .Builder(ImageUploadWorker::class.java)
                .setInputData(inputData)
                .setConstraints(myConstraints)
                .build()
            Timber.d("Create work request done.")
            return workRequest
        }
    }

}