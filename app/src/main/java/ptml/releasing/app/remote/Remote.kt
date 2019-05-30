package ptml.releasing.app.remote

import kotlinx.coroutines.Deferred
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.cargo_search.model.FindCargoResponse
import retrofit2.http.Body

interface Remote {
    suspend fun verifyDeviceIdAsync(imei: String): Deferred<BaseResponse>
    suspend fun loginAsync(username: String?, password: String?): Deferred<BaseResponse>
    suspend fun setAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse>
    suspend fun downloadDamagesAsync(imei: String): Deferred<DamageResponse>
    suspend fun setConfigurationDeviceAsync(
        cargoTypeId: Int?,
        operationStepId: Int?,
        terminal: Int?,
        imei: String
    ): Deferred<ConfigureDeviceResponse>

    suspend fun findCargo(
        cargoTypeId: Int?,
        operationStepId: Int?,
        terminal: Int?,
        imei: String,
        cargoNumber: String
    ): Deferred<FindCargoResponse>


    suspend fun uploadData(request: FormSubmissionRequest,
                           cargoTypeId: Int?,
                           operationStepId: Int?,
                           terminal: Int?,
                           operator:String?,
                           cargoNumber: String?,
                           cargoId: Int?
                           ): Deferred<BaseResponse>
}