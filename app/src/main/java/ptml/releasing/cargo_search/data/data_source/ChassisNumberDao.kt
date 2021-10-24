package ptml.releasing.cargo_search.data.data_source

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ptml.releasing.cargo_search.domain.model.ChassisNumber

@Dao
interface ChassisNumberDao {

    @Query("SELECT * FROM chassisnumber")
    fun getChassisNumbers(): LiveData<List<ChassisNumber>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChassisNumber(chassisNumber: ChassisNumber)

    @Query("DELETE FROM chassisnumber WHERE chasisNumber = :chassisNumber")
    suspend fun deleteChassisNumber(chassisNumber: String?)
}
