package ptml.releasing.data

import io.reactivex.Observable
import ptml.releasing.db.models.user.User
import ptml.releasing.db.models.base.BaseResponse
import ptml.releasing.db.models.config.ConfigurationResponse

interface Repository {

    fun verifyDeviceId( imei: String) : Observable<BaseResponse>

    fun login(user: User):Observable<BaseResponse>

    fun getAdminConfiguration(imei:String):Observable<ConfigurationResponse>
}