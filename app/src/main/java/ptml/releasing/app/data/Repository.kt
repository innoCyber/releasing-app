package ptml.releasing.app.data

import io.reactivex.Observable
import ptml.releasing.auth.model.User
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.admin_configuration.models.ConfigurationResponse

interface Repository {

    fun verifyDeviceId( imei: String) : Observable<BaseResponse>

    fun login(user: User):Observable<BaseResponse>

    fun getAdminConfiguration(imei:String):Observable<ConfigurationResponse>
}