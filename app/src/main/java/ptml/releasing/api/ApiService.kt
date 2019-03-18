package ptml.releasing.api

import io.reactivex.Observable
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/verifyDeviceId")
    fun verifyDeviceId(@Query("imei") imei: String): Observable<BaseResponse>

    @GET("/login")
    fun login(@Query("username") username:String?, @Query("password") password:String?) : Observable<BaseResponse>


    @GET("/setAdminConfiguration")
    fun setAdminConfiguration(@Query("imei") imei: String) : Observable<ConfigurationResponse>

}