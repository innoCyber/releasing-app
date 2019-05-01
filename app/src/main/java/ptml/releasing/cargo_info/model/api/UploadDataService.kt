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
        @Body request: FormSubmissionRequest,
        @Header("cargo_type") cargoTypeId: Int?,
        @Header("operation_step") operationStepId: Int?,
        @Header("terminal") terminal: Int?,
        @Header("operator") operator:String?,
        @Header("cargo_code") cargoCode: String?,
        @Header("cargo_id") cargoId: Int?
    ): Deferred<BaseResponse>
}