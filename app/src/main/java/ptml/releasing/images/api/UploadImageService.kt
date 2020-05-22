package ptml.releasing.images.api

import okhttp3.MultipartBody
import ptml.releasing.app.base.BaseResponse
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UploadImageService {
    @Multipart
    @POST("UploadImage")
    suspend fun upload(
        @Query("cargo_type") cargoTypeId: Int?,
        @Query("cargo_code") cargoCode: String?,
        @Query("cargo_id") cargoId: Int?,
        @Part("PhotoName") imageName: String,
        @Part file: MultipartBody.Part
    ): BaseResponse
}