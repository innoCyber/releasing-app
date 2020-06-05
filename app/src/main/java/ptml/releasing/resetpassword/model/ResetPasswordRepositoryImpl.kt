package ptml.releasing.resetpassword.model

import kotlinx.coroutines.withContext
import ptml.releasing.app.data.domain.model.ApiResult
import ptml.releasing.app.data.domain.repository.ResetPasswordRepository
import ptml.releasing.app.data.remote.mapper.ApiResultMapper
import ptml.releasing.app.data.remote.request.ResetPasswordRequest
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */
class ResetPasswordRepositoryImpl @Inject constructor(
    private val remote: ResetPasswordDataSource.Remote,
    private val dispatchers: AppCoroutineDispatchers,
    private val apiMapper: ApiResultMapper
) : ResetPasswordRepository {

    override suspend fun resetPassword(badgeId: String, password: String, imei: String): ApiResult {
        return withContext(dispatchers.network) {
            val result = remote.resetPassword(ResetPasswordRequest(badgeId, password, imei))
            apiMapper.mapFromModel(result)
        }
    }
}