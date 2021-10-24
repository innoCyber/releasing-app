package ptml.releasing.cargo_search.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.remote.Urls
import ptml.releasing.cargo_search.model.PODResponse
import ptml.releasing.cargo_search.model.FindCargoItems
import ptml.releasing.cargo_search.model.FindCargoResponse
import retrofit2.http.*

interface FindCargoService {
    //@GET(Urls.FIND_CARGO)
    @HTTP(method = "POST", path = Urls.FIND_CARGO, hasBody = true)
    fun findCargo(
        @Header("Authorization") authorization: String,
        @Body findCargoItems: FindCargoItems
    ): Deferred<FindCargoResponse?>?

    @GET(Urls.SETDOWNLOADPOD)
    suspend fun downloadPOD(
        @Header("Authorization") authorization: String,
        @Query("idVoyage")idVoyage: Int
    ): PODResponse

}