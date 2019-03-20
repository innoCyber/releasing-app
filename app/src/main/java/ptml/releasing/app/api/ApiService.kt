package ptml.releasing.app.api

import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/verifyDeviceId")
    fun verifyDeviceId(@Query("imei") imei: String): Deferred<BaseResponse>

    @GET("/login")
    fun login(@Query("username") username:String?, @Query("password") password:String?) : Deferred<BaseResponse>


    @GET("/setAdminConfigurationFail")
    fun setAdminConfiguration(@Query("imei") imei: String) : Deferred<ConfigurationResponse>

}