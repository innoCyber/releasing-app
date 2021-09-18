package ptml.releasing.cargo_search.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.remote.Urls
import ptml.releasing.cargo_search.model.FindCargoItems
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.configuration.models.ShippingLine
import retrofit2.http.*

interface FindCargoService {
    //@GET(Urls.FIND_CARGO)
    @HTTP(method = "POST", path = Urls.FIND_CARGO, hasBody = true)
    fun findCargo(
        @Header("Authorization") authorization: String,
        @Body findCargoItems: FindCargoItems
    ): Deferred<FindCargoResponse?>?

}