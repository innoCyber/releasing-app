package ptml.releasing.app.data.domain.repository

import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.app.data.remote.result.VoyageResult
import ptml.releasing.cargo_search.model.DownloadVoyageResponse
import ptml.releasing.cargo_search.model.PODOperationStep

/**
 * Created by kryptkode on 4/8/2020.
 */
interface VoyageRepository {
    suspend fun getRecentVoyages(): List<ReleasingVoyage>
    suspend fun downloadRecentVoyages(): List<ReleasingVoyage>
    suspend fun downloadAllVoyages(): DownloadVoyageResponse
    suspend fun storeRecentVoyages(voyages: List<ReleasingVoyage>)
    suspend fun getLastSelectedVoyage(): ReleasingVoyage?
    suspend fun setLastSelectedVoyage(voyage: ReleasingVoyage)
}