package ptml.releasing.data

import io.reactivex.Observable
import ptml.releasing.db.models.User
import ptml.releasing.db.models.response.base.BaseResponse

interface Repository {

    fun verifyDeviceId( imei: String) : Observable<BaseResponse>

    fun login(user:User):Observable<BaseResponse>
}