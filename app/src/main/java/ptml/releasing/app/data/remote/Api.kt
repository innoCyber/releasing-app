package ptml.releasing.app.data.remote


import ptml.releasing.app.data.remote.result.AdminOptionsResult
import ptml.releasing.app.data.remote.result._Result
import ptml.releasing.driver.app.data.remote.Endpoints
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.Normalizer

/**
 * Created by kryptkode on 10/23/2019.
 */

interface Api {

    @GET(Endpoints.LOGIN)
    suspend fun login(
        @Query("operation_step") operationStep: Int,
        @Query("terminal") terminal: Int,
        @Query("badgeid") badgeId: String,
        @Query("password") password: String,
        @Query("imei") imei: String?
    ): _Result<Unit>

    @GET(Endpoints.GET_OPERATION_AND_TERMINAL_LISTS)
    suspend fun setAdminConfiguration(@Query("imei") imei: String): AdminOptionsResult

    @GET(Endpoints.SET_CONFIGURATION_DEVICE)
    suspend fun setConfigurationDevice(
        @Query("IdParkingStep") operationStep: Int,
        @Query("imei") imei: String?
    ): _Result<List<Normalizer.Form>>

}