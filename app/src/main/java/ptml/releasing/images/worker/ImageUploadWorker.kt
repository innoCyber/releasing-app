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
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.Constants.UPLOAD_NOTIFICATION_CARGO_CODE
import ptml.releasing.app.utils.Constants.UPLOAD_NOTIFICATION_ID
import ptml.releasing.app.utils.ProgressRequestBody
import ptml.releasing.app.utils.upload.CancelWorkReceiver
import ptml.releasing.app.utils.upload.NotificationHelper
import ptml.releasing.app.utils.upload.NotificationHelper.Companion.IMAGE_NOTIFICATION_ID
import ptml.releasing.app.utils.upload.NotificationHelper.Companion.SUMMARY_NOTIFICATION_ID
import ptml.releasing.device_configuration.view.DeviceConfigActivity
import ptml.releasing.images.api.ImageUploadRepository
import ptml.releasing.images.model.Image
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


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

    override suspend fun doWork(): Result {
        val cargoCode = inputData.getString(CARGO_CODE_KEY)
        val cargoId = inputData.getInt(CARGO_ID_KEY, 0)
        val operationStep = inputData.getInt(OPERATION_STEP_KEY, 0)
        val cargoType = inputData.getInt(CARGO_TYPE_KEY, 0)
        //begin upload for file images
        var result = Result.retry()

        cargoCode?.let { code ->
            val successResponse = mutableListOf<Boolean>()
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
                    val response = uploadImage(image, cargoCode, status, cargoType, cargoId, operationStep, i)
                    val success = response.isSuccess
                    Timber.d("Success: $success")
                    if(success){
                        deleteImageLocally(cargoCode, image)
                    }
                    successResponse.add(success)
                    image.uploaded = success
                    repository.addImage(code, image)
                    result = Result.success()
                }

                /*If any upload fails, it should be retried later*/
                for (success in successResponse) {
                    if (!success) {
                        Timber.d("One upload failed so the work should be retried")
                        result = Result.retry()
                        break
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
                //show the number of failed
                val numberOfFailed = successResponse.filter { !it }.size
                Timber.d("Failed: SIZe: $numberOfFailed")
                showFailureNotification(cargoCode, numberOfFailed, totalImages)
            }

        }
        Timber.w("End of job")
        return result
    }

    private suspend fun deleteImageLocally(cargoCode: String, image: Image): Boolean {
        repository.removeImage(cargoCode, image)
        val file = File(image.imageUri ?: "")
        return file.delete()
    }

    private suspend fun uploadImage(
        image: Image,
        cargoCode: String,
        status: String,
        cargoType: Int,
        cargoId: Int,
        operationStep: Int,
        count: Int
    ): BaseResponse {
        val body = createMultipartBody(image.imageUri, cargoCode, status, cargoId, count)

        return imageUploadRepository.uploadImage(
            cargoType, cargoId, operationStep,
            listOf(createImageFileName(cargoId, count) ?: "image"), body
        )
    }

    /**
     * @param imageUri
     * @return multi part body
     */
    private fun createMultipartBody(
        imageUri: String?,
        cargoCode: String,
        status: String,
        cargoId: Int,
        count: Int
    ): MultipartBody.Part {
        val fileUri = Uri.parse(imageUri)
        val file = File(fileUri.path ?: "")
        Timber.d("createMultipartBody")
        return MultipartBody.Part.createFormData(
            "UploadedImage",
            createImageFileName(cargoId, count),
            createCountingRequestBody(file, fileUri, cargoCode, status)
        )
    }

    private fun createImageFileName(cargoId: Int, count: Int): String? {
        return "${cargoId}_${createTempName()}_${count + 1}.${Constants.IMAGE_EXT}"
    }

    private fun createTempName(): String {
        return SimpleDateFormat("MMddyyyyhhmm", Locale.getDefault()).format(Date())
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
        notificationHelper.notify(IMAGE_NOTIFICATION_ID, notification)
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
        notificationHelper.notify(IMAGE_NOTIFICATION_ID, notification)
        notificationHelper.notify(
            SUMMARY_NOTIFICATION_ID,
            notificationHelper.getSummaryNotification(
                context.getString(R.string.upload_summary_title),
                context.getString(R.string.upload_summary_body)
            )
        )
    }

    private fun showFailureNotification(cargoCode: String?, totalFailed: Int, totalImages: Int) {
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

        val contentText = if (totalImages > 0 && totalFailed > 0) {
            context.getString(R.string.file_upload_failed, totalFailed, totalImages, cargoCode)
        } else {
            context.getString(R.string.file_upload_failed_msg)
        }

        notification = notificationHelper.getNotification(
            context.getString(R.string.message_upload_failed),
            contentText,
            resultPendingIntent
        )

        notification.addAction(
            android.R.drawable.ic_menu_revert,
            context.getString(R.string.cancel_action_text), clearPendingIntent
        )
        notificationHelper.notify(IMAGE_NOTIFICATION_ID, notification)
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