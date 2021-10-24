package ptml.releasing.cargo_search.domain.usecase

import ptml.releasing.cargo_search.domain.model.ChassisNumber
import ptml.releasing.cargo_search.domain.repository.ChassisNumberRepository

class DeleteChassisNumberUseCase(private val chassisNumberRepository: ChassisNumberRepository) {

    suspend operator fun invoke(chassisNumber: String){
        chassisNumberRepository.deleteChassisNumber(chassisNumber)
    }
}