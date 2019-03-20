package ptml.releasing.app.data

import kotlinx.coroutines.Deferred
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.auth.model.User

interface Repository {

    suspend fun verifyDeviceId( imei: String) : Deferred<BaseResponse>

    suspend fun login(user: User):Deferred<BaseResponse>

    suspend fun getAdminConfiguration(imei:String):Deferred<ConfigurationResponse>
}