package ptml.releasing.images.api

import okhttp3.MultipartBody
import ptml.releasing.app.base.BaseResponse
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UploadImageService {

    @Multipart
    @POST("ImageUpload")
    suspend fun upload(
        @Query("cargoid") cargoId:Int,
        @Part("jsondata") data: ImageUploadData,
        @Part files: MultipartBody.Part
    ): BaseResponse
}