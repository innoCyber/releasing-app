package ptml.releasing.cargo_search.data.data_source

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ptml.releasing.cargo_search.domain.model.ChassisNumber
import ptml.releasing.cargo_search.domain.model.ShipSideChassisNumbers

@Dao
interface ChassisNumberDao {

    @Query("SELECT * FROM chassisnumber")
    fun getChassisNumbers(): LiveData<List<ChassisNumber>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChassisNumber(chassisNumber: ChassisNumber)

    @Query("DELETE FROM chassisnumber WHERE chasisNumber = :chassisNumber")
    suspend fun deleteChassisNumber(chassisNumber: String?)


    @Query("SELECT * FROM shipsidechassisnumbers")
    fun getShipSideChassisNumbers(): LiveData<List<ShipSideChassisNumbers>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveShipSideChassisNumber(shipSideChassisNumbers: ShipSideChassisNumbers)

    @Query("DELETE FROM shipsidechassisnumbers WHERE shipSideChassisNumbers = :shipSideChassisNumber")
    suspend fun deleteShipSideChassisNumber(shipSideChassisNumber: String?)
}
