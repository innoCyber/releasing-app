package ptml.releasing.cargo_search.domain.usecase

import kotlinx.coroutines.flow.Flow
import ptml.releasing.cargo_search.domain.model.ChassisNumber
import ptml.releasing.cargo_search.domain.repository.ChassisNumberRepository

class SaveChassisNumberUseCase(private val chassisNumberRepository: ChassisNumberRepository) {

    suspend operator fun invoke(chassisNumber: ChassisNumber){
        return chassisNumberRepository.saveChassisNumber(chassisNumber)
    }
}