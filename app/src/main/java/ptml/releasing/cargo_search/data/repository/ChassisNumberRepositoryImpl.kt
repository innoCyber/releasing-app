package ptml.releasing.cargo_search.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ptml.releasing.cargo_search.data.data_source.ChassisNumberDao
import ptml.releasing.cargo_search.domain.model.ChassisNumber
import ptml.releasing.cargo_search.domain.model.ShipSideChassisNumbers
import ptml.releasing.cargo_search.domain.repository.ChassisNumberRepository

class ChassisNumberRepositoryImpl (private val chassisNumberDao: ChassisNumberDao): ChassisNumberRepository {


    val allChassisNumbers: LiveData<List<ChassisNumber>> = chassisNumberDao.getChassisNumbers()

    override  fun getChassisNumbers(): LiveData<List<ChassisNumber>>{
        return chassisNumberDao.getChassisNumbers()
    }

    override suspend fun saveChassisNumber(chassisNumber: ChassisNumber) {
        chassisNumberDao.saveChassisNumber(chassisNumber)
    }

    override suspend fun deleteChassisNumber(chassisNumber: String?) {
        chassisNumberDao.deleteChassisNumber(chassisNumber)
    }

    override fun getShipSideChassisNumbers(): LiveData<List<ShipSideChassisNumbers>> {
        return chassisNumberDao.getShipSideChassisNumbers()
    }

    override suspend fun saveShipSideChassisNumber(shipSideChassisNumbers: ShipSideChassisNumbers) {
        chassisNumberDao.saveShipSideChassisNumber(shipSideChassisNumbers)
    }

    override suspend fun deleteShipSideChassisNumber(shipSideChassisNumbers: String?) {
        chassisNumberDao.deleteShipSideChassisNumber(shipSideChassisNumbers)
    }
}