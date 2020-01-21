package ptml.releasing.login.model

import kotlinx.coroutines.withContext
import ptml.releasing.app.data.domain.model.ApiResult
import ptml.releasing.app.data.domain.model.login.AdminOptionsEntity
import ptml.releasing.app.data.domain.model.login.LoginEntity
import ptml.releasing.app.data.domain.model.login.OperationStep
import ptml.releasing.app.data.domain.model.login.Terminal
import ptml.releasing.app.data.domain.repository.LoginRepository
import ptml.releasing.app.data.remote.mapper.AdminOptionResultMapper
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
    private val adminOptionsMapper: AdminOptionResultMapper,
    private val dispatchers: AppCoroutineDispatchers
) : LoginRepository {

    override suspend fun authenticate(
        badgeId: String,
        password: String,
        imei: String,
        operationType: OperationStep,
        terminal: Terminal
    ): ApiResult {
        return withContext(dispatchers.network) {
            val result =
                remote.authenticate(
                    LoginRequest(
                        badgeId,
                        password,
                        imei,
                        operationType.id ?: 0,
                        terminal.id ?: 0
                    )
                )
            withContext(dispatchers.db) {
                local.setLoginData(LoginEntity(badgeId, password, imei, operationType, terminal))
            }
            apiMapper.mapFromModel(result)
        }
    }

    override suspend fun getLoginData(): LoginEntity {
        return withContext(dispatchers.db) {
            local.getLoginData()
        }
    }

    override suspend fun setAdminConfiguration(imei: String): AdminOptionsEntity {
        //TODO: Create cache time so refresh from server
        return withContext(dispatchers.db) {
            //check local before remote
            val localAdminOptions = local.getAdminOptions()
            if (localAdminOptions.success == null) {
                //fetch from remote
                val remoteAdminOptions = withContext(dispatchers.network) {
                    val remoteAdminOptionsResult = remote.setAdminConfiguration(imei)
                    adminOptionsMapper.mapFromModel(remoteAdminOptionsResult)
                }

                if (remoteAdminOptions.success == true) {
                    local.setAdminOptions(remoteAdminOptions) //store only a successful response
                }
                remoteAdminOptions
            } else {
                localAdminOptions
            }
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
            local.logOut()
        }
    }

}