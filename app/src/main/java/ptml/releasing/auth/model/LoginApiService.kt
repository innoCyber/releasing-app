package ptml.releasing.auth.model

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginApiService {
    @GET("/login")
    fun loginAsync(@Query("username") username:String?, @Query("password") password:String?) : Deferred<LoginResponse>
}