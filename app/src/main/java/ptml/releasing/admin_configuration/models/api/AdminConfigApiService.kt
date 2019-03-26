package ptml.releasing.admin_configuration.models.api

import kotlinx.coroutines.Deferred
import ptml.releasing.admin_configuration.models.AdminConfigResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AdminConfigApiService {
    @GET("setAdminConfiguration")
    fun setAdminConfigurationAsync(@Query("imei") imei: String) : Deferred<AdminConfigResponse>
}