package ptml.releasing.images.api

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import ptml.releasing.app.base.BaseResponse
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadImageService {
    @Multipart
    @POST("UploadImage")
    fun upload(
        @Part("PhotoName") imageName: String,
        @Part file: MultipartBody.Part
    ): Deferred<BaseResponse>
}