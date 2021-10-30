package ptml.releasing.app.data.remote


import ptml.releasing.app.data.remote.request.ResetPasswordRequest
import ptml.releasing.app.data.remote.request.UpdateAppVersionRequest
import ptml.releasing.app.data.remote.result.VoyageResult
import ptml.releasing.app.data.remote.result._Result
import ptml.releasing.cargo_search.model.DownloadVoyageResponse
import ptml.releasing.cargo_search.model.PODOperationStep
import retrofit2.http.*

/**
 * Created by kryptkode on 10/23/2019.
 */

interface Api {

    @GET(Endpoints.LOGIN)
    suspend fun login(
        @Header("Authorization") authorization: String,
        @Query("badge_id") badgeId: String,
        @Query("password") password: String
    ): _Result<Unit>

    @POST(Endpoints.RESET_PASSWORD)
    suspend fun resetPassword(@Header("Authorization") authorization: String,
                                  @Body request: List<ResetPasswordRequest>): _Result<Unit>

    @GET(Endpoints.GET_RECENT_VOYAGES)
    suspend fun getRecentVoyages(
        @Header("Authorization") authorization: String,
        @Query("imei") imei: String?
    ): VoyageResult

    @GET(Endpoints.GET_RECENT_VOYAGES)
    suspend fun getAllVoyages(
        @Header("Authorization") authorization: String,
        @Query("imei") imei: String?
    ): DownloadVoyageResponse


    @POST(Endpoints.UPDATE_APP_VERSION_INSTALLED)
    suspend fun updateAppVersion(@Header("Authorization") authorization: String,
                                 @Body request: List<UpdateAppVersionRequest>): _Result<Unit>

}