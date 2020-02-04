package ptml.releasing.app.data.domain.usecase

import ptml.releasing.app.data.domain.repository.LoginRepository
import javax.inject.Inject

/**
 * Created by kryptkode on 11/14/2019.
 */
class SetLoggedInUseCase @Inject constructor(private val repository: LoginRepository) :
    UseCase<Unit, SetLoggedInUseCase.Params>() {

    override suspend fun buildUseCase(params: Params?) {
        requireNotNull(params) { "Params can't be null!" }
        return repository.setLoggedIn(params.loggedIn)
    }

    data class Params(val loggedIn: Boolean)
}