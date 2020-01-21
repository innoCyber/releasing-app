package ptml.releasing.app.data.domain.usecase

import ptml.releasing.app.data.domain.model.login.LoginEntity
import ptml.releasing.app.data.domain.repository.LoginRepository
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */

class GetLoginUseCase @Inject constructor(private val loginRepository: LoginRepository) :
    UseCase<LoginEntity, Unit>() {

    override suspend fun buildUseCase(params: Unit?): LoginEntity {
        return loginRepository.getLoginData()
    }
}