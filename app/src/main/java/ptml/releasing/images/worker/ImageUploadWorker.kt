package ptml.releasing.images.worker

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


/**
Created by kryptkode on 8/6/2019
 */
class ImageUploadWorker @AssistedInject constructor(
    @VisibleForTesting val repository: Repository,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        val cargoCode = inputData.getString(CARGO_CODE_KEY)
        //begin upload for file images
        var result = Result.retry()
        val successResponse = mutableListOf<Boolean>()
        cargoCode?.let { code ->
            Timber.d("Starting work")
            try {
                val images = repository.getImages(code)
                images.values.forEach {
                    if (it.isFile() && !it.uploaded) {
                        val response = uploadImageAsync(it).await()
                        val success = response.isSuccess
                        Timber.d("Success: $success")
                        successResponse.add(success)
                        it.uploaded = success
                        repository.addImage(cargoCode, it)
                    }
                    result = Result.success()
                }

                /*If any upload fails, it should be retried later*/
                for (success in successResponse) {
                    if(!success){
                        Timber.d("One upload failed so the work should be retried")
                        result = Result.retry()
                        break
                    }
                }

            } catch (e: Throwable) {
                Timber.e(e)
                result = Result.retry()
            }
        }
        return result
    }

    private suspend fun uploadImageAsync(it: Image): Deferred<BaseResponse> {
        val fileUri = Uri.parse(it.imageUri)
        val file = File(fileUri.path ?: "")
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
            MediaType.parse(applicationContext.contentResolver.getType(fileUri) ?: ""),
            file
        )
        // MultipartBody.Part is used to send also the actual file name
        //TODO: Change the name
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        return repository.uploadImage(it.name ?: "", body)
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