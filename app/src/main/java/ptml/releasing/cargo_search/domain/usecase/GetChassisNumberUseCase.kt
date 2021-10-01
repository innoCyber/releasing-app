package ptml.releasing.cargo_search.domain.usecase

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ptml.releasing.cargo_search.domain.model.ChassisNumber
import ptml.releasing.cargo_search.domain.repository.ChassisNumberRepository

class GetChassisNumberUseCase(private val chassisNumberRepository: ChassisNumberRepository) {

    suspend operator fun invoke(): LiveData<List<ChassisNumber>> {
        return chassisNumberRepository.getChassisNumbers()
    }
}