package ptml.releasing.images.api

import okhttp3.MultipartBody
import ptml.releasing.app.base.BaseResponse
import javax.inject.Inject

class ImageUploadRepository @Inject constructor(private val imageRestClient: ImageRestClient) {

    suspend fun uploadImage(
        cargoTypeId: Int?,
        cargoId: Int?,
        operationStep:Int,
        fileNames: List<String>,
        files: List<MultipartBody.Part>
    ): BaseResponse {
        val data = ImageUploadData(cargoTypeId, operationStep, cargoId, fileNames.map { ImageFileName(it) })
        return imageRestClient.getRemoteCaller()
            .upload(data,  files)
    }
}