package ptml.releasing.app.data.domain.usecase

import ptml.releasing.app.data.domain.model.ApiResult
import ptml.releasing.app.data.domain.model.login.OperationStep
import ptml.releasing.app.data.domain.model.login.Terminal
import ptml.releasing.app.data.domain.repository.LoginRepository
import javax.inject.Inject


/**
 * Created by kryptkode on 10/23/2019.
 */

class LoginUseCase @Inject constructor(private val loginRepository: LoginRepository) :
    UseCase<ApiResult?, LoginUseCase.Params>() {

    override suspend fun buildUseCase(params: Params?): ApiResult? {
        requireNotNull(params) { "Params can't be null!" }
        return loginRepository.authenticate(
            params.badgeId,
            params.password,
            params.imei,
            params.operationType,
            params.terminal
        )
    }

    data class Params(
        val badgeId: String,
        val password: String,
        val imei: String,
        val operationType: OperationStep,
        val terminal: Terminal
    )
}