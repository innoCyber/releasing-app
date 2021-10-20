package ptml.releasing.cargo_info.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.remote.Urls
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import retrofit2.http.*

interface UploadDataService {
    @POST(Urls.UPLOAD_DATA)
    fun uploadData(
        @Header("Authorization") authorization: String,
        @Body request: FormSubmissionRequest
    ): Deferred<BaseResponse>

//    @GET(Urls.SETDOWNLOADPOD)
//    suspend fun downloadPOD(
//        @Header("Authorization") authorization: String,
//        @Query("idVoyage")idVoyage: Int
//    ): PODResponse
}