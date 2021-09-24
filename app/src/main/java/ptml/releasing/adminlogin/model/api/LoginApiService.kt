package ptml.releasing.adminlogin.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.remote.Urls
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface LoginApiService {
    @GET("{endPoint}")
    fun loginAsync( @Path("endPoint") endPoint: String, @Header("Authorization") authorization: String, @Query("username") username:String?, @Query("password") password:String?) : Deferred<BaseResponse>
}


