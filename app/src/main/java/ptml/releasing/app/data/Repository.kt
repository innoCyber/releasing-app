package ptml.releasing.app.data

import kotlinx.coroutines.Deferred
import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.admin_configuration.models.Configuration
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.auth.model.User
import ptml.releasing.damages.model.DamageResponse

interface Repository {

    suspend fun verifyDeviceIdAsync(imei: String): Deferred<BaseResponse>

    suspend fun loginAsync(user: User): Deferred<BaseResponse>

    suspend fun getAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse>
    suspend fun downloadAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse>

    suspend fun downloadDamagesAsync(imei: String): Deferred<DamageResponse>
    suspend fun getDamagesAsync(imei: String): Deferred<DamageResponse>

    suspend fun getSavedConfigAsync(): Deferred<Configuration>
    suspend fun setSavedConfigAsync(configuration: Configuration)

    suspend fun isFirstAsync(): Deferred<Boolean>
    suspend fun setFirst(value: Boolean)
    suspend fun isConfiguredAsync(): Deferred<Boolean>
    suspend fun setConfigured(isConfigured: Boolean)
}