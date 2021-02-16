package ptml.releasing.app.data.remote


import ptml.releasing.app.data.remote.request.ResetPasswordRequest
import ptml.releasing.app.data.remote.request.UpdateAppVersionRequest
import ptml.releasing.app.data.remote.result.VoyageResult
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
        @Query("badge_id") badgeId: String,
        @Query("password") password: String
    ): _Result<Unit>

    @POST(Endpoints.RESET_PASSWORD)
    suspend fun resetPassword(@Body request: List<ResetPasswordRequest>): _Result<Unit>

    @GET(Endpoints.GET_RECENT_VOYAGES)
    suspend fun getRecentVoyages(
        @Query("imei") imei: String?
    ): VoyageResult


    @POST(Endpoints.UPDATE_APP_VERSION_INSTALLED)
    suspend fun updateAppVersion(@Body request: List<UpdateAppVersionRequest>): _Result<Unit>

}