package ptml.releasing.adminlogin.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.remote.Urls
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginApiService {
    @GET(Urls.LOGIN)
    fun loginAsync(@Query("username") username:String?, @Query("password") password:String?) : Deferred<BaseResponse>
}