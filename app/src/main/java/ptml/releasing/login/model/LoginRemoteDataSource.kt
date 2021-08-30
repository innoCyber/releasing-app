package ptml.releasing.login.model

import ptml.releasing.app.data.local.LocalDataManager
import ptml.releasing.app.data.remote.RestClient
import ptml.releasing.app.data.remote.request.LoginRequest
import ptml.releasing.app.data.remote.request.UpdateAppVersionRequest
import ptml.releasing.app.data.remote.result._Result
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */

class LoginRemoteDataSource @Inject constructor(
    private val restClient: RestClient,
    private val localDataManager: LocalDataManager
) :
    LoginDataSource.Remote {

    override suspend fun authenticate(loginRequest: LoginRequest): _Result<Unit> {
        return restClient.getRemoteCaller().login(
            localDataManager.getStaticAuth(),
            loginRequest.badgeId,
            loginRequest.password
        )
    }

    override suspend fun updateAppVersion(updateAppVersionRequest: UpdateAppVersionRequest): _Result<Unit> {
        return restClient.getRemoteCaller().updateAppVersion(localDataManager.getStaticAuth(),
            listOf(updateAppVersionRequest)
        )
    }

}