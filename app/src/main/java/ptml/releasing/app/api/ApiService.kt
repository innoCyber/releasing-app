package ptml.releasing.app.api

import kotlinx.coroutines.Deferred
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import ptml.releasing.app.base.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/verifyDeviceId")
    fun verifyDeviceId(@Query("imei") imei: String): Deferred<BaseResponse>

    @GET("/login")
    fun login(@Query("username") username:String?, @Query("password") password:String?) : Deferred<BaseResponse>


    @GET("/setAdminConfiguration")
    fun setAdminConfiguration(@Query("imei") imei: String) : Deferred<ConfigurationResponse>

}