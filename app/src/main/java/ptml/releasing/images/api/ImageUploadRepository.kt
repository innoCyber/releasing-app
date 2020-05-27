package ptml.releasing.images.api

import okhttp3.MultipartBody
import ptml.releasing.app.base.BaseResponse
import javax.inject.Inject

class ImageUploadRepository @Inject constructor(private val imageRestClient: ImageRestClient) {

    suspend fun uploadImage(
        cargoTypeId: Int?,
        cargoCode: String?,
        cargoId: Int?,
        imageName: String,
        file: MultipartBody.Part
    ): BaseResponse {
        return imageRestClient.getRemoteCaller()
            .upload(cargoTypeId, cargoCode, cargoId, imageName, file)
    }
}