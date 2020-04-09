package ptml.releasing.app.data.remote.mapper

import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.app.data.remote.result.VoyageRemote
import ptml.releasing.driver.app.data.remote.mapper.ModelMapper
import javax.inject.Inject

/**
 * Created by kryptkode on 4/8/2020.
 */
class VoyageMapper @Inject constructor() : ModelMapper<VoyageRemote, ReleasingVoyage> {

    override fun mapFromModel(model: VoyageRemote): ReleasingVoyage {
        return ReleasingVoyage(model.voyageNumber)
    }
}