package ptml.releasing.app.remote

import kotlinx.coroutines.Deferred
import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.damages.model.DamageResponse

interface Remote {
   suspend fun verifyDeviceIdAsync(imei:String): Deferred<BaseResponse>
   suspend fun loginAsync(username:String?,  password:String?):Deferred<BaseResponse>
   suspend fun setAdminConfigurationAsync(imei:String):Deferred<AdminConfigResponse>
   suspend fun downloadDamagesAsync(imei:String):Deferred<DamageResponse>
}