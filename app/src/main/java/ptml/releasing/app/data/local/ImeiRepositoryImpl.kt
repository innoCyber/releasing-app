package ptml.releasing.app.data.local

import kotlinx.coroutines.withContext
import ptml.releasing.app.data.domain.repository.ImeiRepository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

/**
 * Created by kryptkode on 1/21/2020.
 */
class ImeiRepositoryImpl @Inject constructor(
    private val localDataManager: LocalDataManager,
    private val dispatchers: AppCoroutineDispatchers
) : ImeiRepository {

    override suspend fun getIMEI(): String {
        return withContext(dispatchers.db) {
            localDataManager.getIMEI()
        }
    }

    override suspend fun setIMEI(imei: String) {
        return withContext(dispatchers.db) {
            localDataManager.setIMEI(imei)
        }
    }
}