package ptml.releasing.app.data.remote


import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.app.data.remote.request.ResetPasswordRequest
import ptml.releasing.app.data.remote.result._Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST(Endpoints.RESET_PASSWORD)
    suspend fun resetPassword(@Body request: ResetPasswordRequest): _Result<Unit>

    @GET(Endpoints.GET_RECENT_VOYAGES)
    suspend fun getRecentVoyages(
        @Query("imei") imei: String?
    ): _Result<ReleasingVoyage>

}