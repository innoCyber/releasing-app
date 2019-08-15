package ptml.releasing.images.worker

import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.work.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Deferred
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.data.Repository
import ptml.releasing.app.di.modules.worker.ChildWorkerFactory
import ptml.releasing.images.model.Image
import timber.log.Timber
import java.io.File
import ptml.releasing.app.utils.CountingRequestBody
import android.content.Intent
import androidx.core.app.NotificationCompat
import ptml.releasing.R
import ptml.releasing.app.utils.Constants.UPLOAD_NOTIFICATION_CARGO_CODE
import ptml.releasing.app.utils.Constants.UPLOAD_NOTIFICATION_ID
import ptml.releasing.app.utils.upload.NotificationHelper
import ptml.releasing.app.utils.upload.NotificationHelper.Companion.NOTIFICATION_ID
import ptml.releasing.app.utils.upload.CancelWorkReceiver
import ptml.releasing.device_configuration.view.DeviceConfigActivity


/**
Created by kryptkode on 8/6/2019
 */
class ImageUploadWorker @AssistedInject constructor(
    @VisibleForTesting val repository: Repository,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    val mNotificationHelper = NotificationHelper(context)
    lateinit var notification: NotificationCompat.Builder

    override suspend fun doWork(): Result {
        val cargoCode = inputData.getString(CARGO_CODE_KEY)
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
                        val response = uploadImageAsync(image, cargoCode, status).await()
                        val success = response.isSuccess
                        Timber.d("Success: $success")
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
                }

            } catch (e: Throwable) {
                Timber.e(e)
                result = Result.retry()
            }


            if (result == Result.success()) {
                showSuccessNotification(cargoCode)
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

    private suspend fun uploadImageAsync(
        it: Image,
        cargoCode: String,
        status: String
    ): Deferred<BaseResponse> {
        val body = createMultipartBody(it.imageUri, cargoCode, status)
        return repository.uploadImage(it.name ?: "", body)
    }


    /**
     * @param filePath
     * @return multi part body
     */
    private fun createMultipartBody(
        imageUri: String?,
        cargoCode: String,
        status: String
    ): MultipartBody.Part {
        val fileUri = Uri.parse(imageUri)
        val file = File(fileUri.path ?: "")
        return MultipartBody.Part.createFormData(
            "image",
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
        return CountingRequestBody(requestBody,
            CountingRequestBody.Listener { bytesWritten, contentLength ->
                val progress = 1.0 * bytesWritten / contentLength
                Timber.w("Progress: $progress")
                sendProgressBroadcast(progress, file.name, cargoCode, status)
            })
    }

    private fun sendProgressBroadcast(
        progress: Double,
        name: String,
        cargoCode: String,
        status: String
    ) {
        notification = mNotificationHelper.getNotification(
            context.getString(R.string.uploading, status),
            context.getString(R.string.in_progress, name),
            (100 * progress).toInt()
        )
        mNotificationHelper.notify(NOTIFICATION_ID, notification)
//        localBroadcastManager.sendBroadcast(progressIntent)
    }

    private fun createRequestBodyFromFile(file: File, mimeType: String): RequestBody {
        return RequestBody.create(MediaType.parse(mimeType), file)
    }

    /**
     * Send Broadcast to FileProgressReceiver while file upload successful
     */
    private fun showSuccessNotification(cargoCode: String?) {
        val resultIntent = Intent(context, DeviceConfigActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
            context,
            0 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notification = mNotificationHelper.getNotification(
            context.getString(R.string.message_upload_success),
            context.getString(R.string.file_upload_successful, cargoCode),
            resultPendingIntent
        )
        mNotificationHelper.notify(NOTIFICATION_ID, notification)
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

        clearIntent.putExtra(UPLOAD_NOTIFICATION_ID, NOTIFICATION_ID)
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

        notification = mNotificationHelper.getNotification(
            context.getString(R.string.message_upload_failed),
            contentText,
            resultPendingIntent
        )

        notification.addAction(android.R.drawable.ic_menu_revert,
            context.getString(R.string.cancel_action_text), clearPendingIntent);
        mNotificationHelper.notify(NOTIFICATION_ID, notification)
    }


    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    companion object {

        const val CARGO_CODE_KEY = "cargo_code_key"

        fun createWorkRequest(cargoCode: String): OneTimeWorkRequest {

            val myConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val inputData = Data.Builder()
                .putString(CARGO_CODE_KEY, cargoCode)
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