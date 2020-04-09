package ptml.releasing.app.data.repo

import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.app.data.domain.repository.VoyageRepository
import ptml.releasing.app.data.local.LocalDataManager
import ptml.releasing.app.data.remote.RestClient
import ptml.releasing.app.data.remote.mapper.VoyageMapper

/**
 * Created by kryptkode on 4/8/2020.
 */
class VoyageRepositoryImpl(
    private val local: LocalDataManager,
    private val remote: RestClient,
    private val voyageMapper: VoyageMapper
) : VoyageRepository {

    override suspend fun getRecentVoyages(): List<ReleasingVoyage> {
        return if (local.getRecentVoyages().isEmpty()) {
            val deviceId = local.getIMEI()
            val result = remote.getRemoteCaller().getRecentVoyages(deviceId)
            val data = result.data?.map {
                voyageMapper.mapFromModel(it)
            } ?: listOf()
            local.setRecentVoyages(data)
            data
        } else {
            local.getRecentVoyages()
        }
    }

    override suspend fun storeRecentVoyages(voyages: List<ReleasingVoyage>) {
        return local.setRecentVoyages(voyages)
    }

    override suspend fun getLastSelectedVoyage(): ReleasingVoyage? {
        return local.getLastSelectedVoyage()
    }

    override suspend fun setLastSelectedVoyage(voyage: ReleasingVoyage) {
        return local.setLastSelectedVoyage(voyage)
    }
}