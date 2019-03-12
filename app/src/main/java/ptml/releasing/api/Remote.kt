package ptml.releasing.api

import io.reactivex.Observable
import ptml.releasing.db.models.response.base.BaseResponse

interface Remote {
    fun verifyDeviceId(imei:String):Observable<BaseResponse>
    fun login(username:String,  password:String):Observable<BaseResponse>
}