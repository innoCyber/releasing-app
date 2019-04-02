package ptml.releasing.app.api

import kotlinx.coroutines.Deferred
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import ptml.releasing.app.base.BaseResponse

interface Remote {
   suspend fun verifyDeviceId(imei:String): Deferred<BaseResponse>
   suspend fun login(username:String?,  password:String?):Deferred<BaseResponse>
   suspend fun setAdminConfiguration(imei:String):Deferred<ConfigurationResponse>
}