package ptml.releasing.app.remote

import kotlinx.coroutines.Deferred
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.auth.model.LoginResponse
import ptml.releasing.device_configuration.model.DeviceConfigResponse

interface Remote {
   suspend fun verifyDeviceIdAsync(imei:String): Deferred<DeviceConfigResponse>
   suspend fun loginAsync(username:String?,  password:String?):Deferred<LoginResponse>
   suspend fun setAdminConfigurationAsync(imei:String):Deferred<ConfigurationResponse>
}