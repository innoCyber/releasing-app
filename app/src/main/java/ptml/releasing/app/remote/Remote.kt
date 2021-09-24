package ptml.releasing.app.remote

import kotlinx.coroutines.Deferred
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.quick_remarks.model.QuickRemarkResponse

interface Remote {
    suspend fun verifyDeviceIdAsync(imei: String): Deferred<BaseResponse>
    suspend fun loginAsync(endpoint: String, authentication: String, username: String?, password: String?): Deferred<BaseResponse>
    suspend fun getAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse>
    suspend fun downloadDamagesAsync(imei: String): Deferred<DamageResponse>
    suspend fun downloadQuickRemarkAsync(imei: String): Deferred<QuickRemarkResponse>
    suspend fun setConfigurationDeviceAsync(
        cargoTypeId: Int?,
        operationStepId: Int?,
        terminal: Int?,
        imei: String
    ): Deferred<ConfigureDeviceResponse>

    suspend fun findCargo(
        cargoTypeId: String?,
        operationStepId: Int?,
        terminal: Int?,
        shippingLine: String?,
        voyage: Int?,
        imei: String,
        cargoNumber: String,
        id_voyage: Int
    ): Deferred<FindCargoResponse?>?

    suspend fun uploadData(
        request: FormSubmissionRequest
    ): Deferred<BaseResponse>
}