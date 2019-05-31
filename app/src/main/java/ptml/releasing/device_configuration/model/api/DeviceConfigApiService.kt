package ptml.releasing.device_configuration.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.base.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeviceConfigApiService {
    @GET("verifyDeviceId")
    fun verifyDeviceIdAsync(@Query("imei") imei: String): Deferred<BaseResponse>
}