package ptml.releasing.configuration.models.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.remote.Urls
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ConfigApiService {
    @GET(Urls.GET_ADMIN_CONFIGURATION)
    fun getAdminConfigurationAsync(
        @Header("Authorization") authorization: String,
        @Query("imei") imei: String
    ): Deferred<AdminConfigResponse>

    @GET(Urls.SET_CONFIGURATION_DEVICE)
    fun setConfigurationDeviceAsync(
        @Header("Authorization") authorization: String,
        @Query("operation_step") operationStepId: Int?,
        @Query("imei") imei: String
    ): Deferred<ConfigureDeviceResponse>
}//65