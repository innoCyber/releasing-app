package ptml.releasing.app.data.domain.usecase

import ptml.releasing.app.data.domain.repository.ImeiRepository
import javax.inject.Inject

/**
 * Created by kryptkode on 11/14/2019.
 */
class GetImeiUseCase
@Inject constructor(private val repository: ImeiRepository) : UseCase<String, Unit>() {

    override suspend fun buildUseCase(params: Unit?): String {
        return repository.getIMEI()
    }
}