package ptml.releasing.device_configuration.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.remote.Urls
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DeviceConfigApiService {
    @GET(Urls.VERIFY_DEVICE)
    fun verifyDeviceIdAsync(@Header("Authorization") authorization: String,
                            @Query("imei") imei: String): Deferred<BaseResponse>
}