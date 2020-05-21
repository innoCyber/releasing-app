package ptml.releasing.app.data.domain.usecase

import ptml.releasing.app.data.domain.repository.LoginRepository
import javax.inject.Inject

/**
 * Created by kryptkode on 11/14/2019.
 */
class LogOutUseCase @Inject constructor(private val loginRepository: LoginRepository) :
    UseCase<Boolean, Unit>() {
    override suspend fun buildUseCase(params: Unit?): Boolean {
        return loginRepository.logOut()
    }
}