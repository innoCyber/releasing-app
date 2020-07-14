package ptml.releasing.images.api

import okhttp3.MultipartBody
import ptml.releasing.app.base.BaseResponse
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadImageService {

    @Multipart
    @POST("ImagesUpload")
    suspend fun upload(
        @Part("jsondata") data: ImageUploadData,
        @Part files: List<MultipartBody.Part>
    ): BaseResponse
}