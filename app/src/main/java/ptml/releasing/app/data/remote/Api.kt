package ptml.releasing.app.data.remote


import ptml.releasing.app.data.remote.result._Result
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by kryptkode on 10/23/2019.
 */

interface Api {

    @GET(Endpoints.LOGIN)
    suspend fun login(
        @Query("badgeid") badgeId: String,
        @Query("password") password: String,
        @Query("imei") imei: String?
    ): _Result<Unit>

}