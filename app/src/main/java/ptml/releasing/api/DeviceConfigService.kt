package ptml.releasing.api

import io.reactivex.Observable
import io.reactivex.Single
import ptml.releasing.db.models.response.base.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeviceConfigService {

    @GET("/verifyDeviceId")
    fun verifyDeviceId(@Query("imei") imei: String): Observable<BaseResponse>
}