package ptml.releasing.app.remote

import kotlinx.coroutines.Deferred
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.damages.model.DamageResponse
import retrofit2.http.Query

interface Remote {
    suspend fun verifyDeviceIdAsync(imei: String): Deferred<BaseResponse>
    suspend fun loginAsync(username: String?, password: String?): Deferred<BaseResponse>
    suspend fun setAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse>
    suspend fun downloadDamagesAsync(imei: String): Deferred<DamageResponse>
    suspend fun setConfigurationDeviceAsync(
        cargoTypeId: Int,
        operationStepId: Int,
        terminal: Int,
        imei: String
    ): Deferred<ConfigureDeviceResponse>
}