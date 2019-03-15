package ptml.releasing.api

import io.reactivex.Observable
import ptml.releasing.db.models.base.BaseResponse
import ptml.releasing.db.models.config.ConfigurationResponse

interface Remote {
    fun verifyDeviceId(imei:String):Observable<BaseResponse>
    fun login(username:String?,  password:String?):Observable<BaseResponse>
    fun setAdminConfiguration(imei:String):Observable<ConfigurationResponse>
}