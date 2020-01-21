package ptml.releasing.app.data.domain.usecase

import ptml.releasing.app.data.domain.repository.ImeiRepository
import javax.inject.Inject

/**
 * Created by kryptkode on 11/14/2019.
 */
class SetImeiUseCase @Inject constructor(
    private val repository: ImeiRepository
) : UseCase<Unit, SetImeiUseCase.Params>() {


    override suspend fun buildUseCase(params: Params?) {
        requireNotNull(params) { "Params should not be null" }
        return repository.setIMEI(params.imei)
    }

    data class Params(val imei: String)
}