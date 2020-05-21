package ptml.releasing.cargo_search.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.remote.Urls
import ptml.releasing.cargo_search.model.FindCargoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FindCargoService {
    @GET(Urls.FIND_CARGO)
    fun findCargo(
            @Query("cargo_type") cargoTypeId: Int?,
            @Query("operation_step") operationStepId: Int?,
            @Query("terminal") terminal: Int?,
            @Query("imei") imei: String,
            @Query("cargo_code")cargoNumber:String
    ): Deferred<FindCargoResponse?>?
}