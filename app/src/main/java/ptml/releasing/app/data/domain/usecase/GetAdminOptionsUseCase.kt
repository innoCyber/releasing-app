package ptml.releasing.app.data.domain.usecase

import ptml.releasing.app.data.domain.model.login.AdminOptionsEntity
import ptml.releasing.app.data.domain.repository.LoginRepository
import javax.inject.Inject


/**
 * Created by kryptkode on 10/27/2019.
 */
class GetAdminOptionsUseCase @Inject constructor(private val loginRepository: LoginRepository) :
    UseCase<AdminOptionsEntity, GetAdminOptionsUseCase.Params>() {

    override suspend fun buildUseCase(params: Params?): AdminOptionsEntity {
        requireNotNull(params) { "Params can't be null!" }
        return loginRepository.setAdminConfiguration(params.imei)
    }

    data class Params(
        val imei: String
    )
}