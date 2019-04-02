package ptml.releasing.damages.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.damages.model.DamageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DamageApiService {
    @GET("downloadDamages")
    fun downloadDamagesAsync(@Query("imei") imei: String): Deferred<DamageResponse>
}