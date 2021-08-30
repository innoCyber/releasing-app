package ptml.releasing.download_damages.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.remote.Urls
import ptml.releasing.download_damages.model.DamageResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DamageApiService {
    @GET(Urls.DOWNLOAD_DAMAGES)
    fun downloadDamagesAsync(@Header("Authorization") authorization: String,
                             @Query("imei") imei: String): Deferred<DamageResponse>
}