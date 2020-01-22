package ptml.releasing.login.model

import kotlinx.coroutines.withContext
import ptml.releasing.app.data.domain.model.ApiResult
import ptml.releasing.app.data.domain.model.login.LoginEntity
import ptml.releasing.app.data.domain.repository.LoginRepository
import ptml.releasing.app.data.remote.mapper.ApiResultMapper
import ptml.releasing.app.data.remote.request.LoginRequest
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */

class LoginRepositoryImpl @Inject constructor(
    private val local: LoginDataSource.Local,
    private val remote: LoginDataSource.Remote,
    private val apiMapper: ApiResultMapper,
    private val dispatchers: AppCoroutineDispatchers
) : LoginRepository {

    override suspend fun authenticate(
        badgeId: String,
        password: String,
        imei: String
    ): ApiResult {
        return withContext(dispatchers.network) {
            val result =
                remote.authenticate(
                    LoginRequest(
                        badgeId,
                        password,
                        imei
                    )
                )
            withContext(dispatchers.db) {
                local.setLoginData(LoginEntity(badgeId, password, imei))
            }
            apiMapper.mapFromModel(result)
        }
    }

    override suspend fun getLoginData(): LoginEntity {
        return withContext(dispatchers.db) {
            local.getLoginData()
        }
    }

    override suspend fun setLoggedIn(value: Boolean) {
        return withContext(dispatchers.db) {
            local.setLoggedIn(value)
        }
    }

    override suspend fun getLoggedIn(): Boolean {
        return withContext(dispatchers.db) {
            local.getLoggedIn()
        }
    }

    override suspend fun logOut(): Boolean {
        return withContext(dispatchers.db) {
            local.logOutUser()
        }
    }

}