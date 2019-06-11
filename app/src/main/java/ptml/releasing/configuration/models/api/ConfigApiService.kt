package ptml.releasing.configuration.models.api

import kotlinx.coroutines.Deferred
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ConfigApiService {
    @GET("setAdminConfiguration")
    fun setAdminConfigurationAsync(
        @Query("imei") imei: String
    ): Deferred<AdminConfigResponse>

    @GET("setConfigurationDevice")
    fun
            setConfigurationDeviceAsync(
//        @Query("cargo_type") cargoTypeId: Int,
        @Query("operation_step") operationStepId: Int?,
//        @Query("terminal") terminal: Int,
        @Query("imei") imei: String
    ): Deferred<ConfigureDeviceResponse>
}