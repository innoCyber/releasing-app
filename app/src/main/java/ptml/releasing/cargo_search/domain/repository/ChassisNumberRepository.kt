package ptml.releasing.cargo_search.domain.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ptml.releasing.cargo_search.domain.model.ChassisNumber

interface ChassisNumberRepository{

    fun getChassisNumbers(): LiveData<List<ChassisNumber>>

    suspend fun saveChassisNumber(chassisNumber: ChassisNumber)

    suspend fun deleteChassisNumber(chassisNumber: String?)
}