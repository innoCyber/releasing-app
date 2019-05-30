package ptml.releasing.cargo_info.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UploadDataService {
    @POST("uploadData")
    fun uploadData(
        @Body request: FormSubmissionRequest
    ): Deferred<BaseResponse>
}