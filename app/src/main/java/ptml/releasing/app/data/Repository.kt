package ptml.releasing.app.data

import kotlinx.coroutines.Deferred
import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.auth.model.LoginResponse
import ptml.releasing.auth.model.User
import ptml.releasing.damages.model.DamageResponse
import ptml.releasing.device_configuration.model.DeviceConfigResponse

interface Repository {

    suspend fun verifyDeviceIdAsync( imei: String) :Deferred<DeviceConfigResponse>

    suspend fun loginAsync(user: User):Deferred<LoginResponse>

    suspend fun getAdminConfigurationAsync(imei:String):Deferred<AdminConfigResponse>
    suspend fun downloadAdminConfigurationAsync(imei:String):Deferred<AdminConfigResponse>

    suspend fun downloadDamagesAsync(imei:String):Deferred<DamageResponse>
    suspend fun getDamagesAsync(imei:String):Deferred<DamageResponse>
}