package ptml.releasing.api

import io.reactivex.Observable
import io.reactivex.Single
import ptml.releasing.db.models.response.base.BaseResponse

interface Remote {
    fun verifyDeviceId(imei:String):Single<BaseResponse>
}