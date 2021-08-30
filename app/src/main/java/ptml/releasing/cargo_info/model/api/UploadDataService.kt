package ptml.releasing.cargo_info.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.remote.Urls
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UploadDataService {
    @POST(Urls.UPLOAD_DATA)
    fun uploadData(
        @Header("Authorization") authorization: String,
        @Body request: FormSubmissionRequest
    ): Deferred<BaseResponse>
}