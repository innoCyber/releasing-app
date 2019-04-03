package ptml.releasing.admin_configuration.models.api

import kotlinx.coroutines.Deferred
import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.admin_configuration.models.ConfigureDeviceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AdminConfigApiService {
    @GET("setAdminConfiguration")
    fun setAdminConfigurationAsync(
        @Query("imei") imei: String
    ): Deferred<AdminConfigResponse>

    @GET("setConfigurationDevice")
    fun setConfigurationDeviceAsync(
        @Query("cargo_type") cargoTypeId: Int,
        @Query("operation_step") operationStepId: Int,
        @Query("terminal") terminal: Int,
        @Query("imei") imei: String
    ): Deferred<ConfigureDeviceResponse>
}