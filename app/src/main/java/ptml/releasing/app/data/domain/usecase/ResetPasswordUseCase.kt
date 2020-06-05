package ptml.releasing.app.data.domain.usecase

import ptml.releasing.app.data.domain.model.ApiResult
import ptml.releasing.app.data.domain.repository.ResetPasswordRepository
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */
class ResetPasswordUseCase @Inject constructor(
    private val repository: ResetPasswordRepository
) : UseCase<ApiResult?, ResetPasswordUseCase.Params>() {

    override suspend fun buildUseCase(params: Params?): ApiResult? {
        requireNotNull(params) { "Params can't be null!" }
        return repository.resetPassword(
            params.badgeId,
            params.password,
            params.imei
        )
    }

    data class Params(
        val badgeId: String,
        val password: String,
        val imei: String
    )
}