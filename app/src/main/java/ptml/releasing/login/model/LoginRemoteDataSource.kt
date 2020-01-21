package ptml.releasing.login.model

import ptml.releasing.app.data.remote.RestClient
import ptml.releasing.app.data.remote.request.LoginRequest
import ptml.releasing.app.data.remote.result.AdminOptionsResult
import ptml.releasing.app.data.remote.result._Result
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */

class LoginRemoteDataSource @Inject constructor(private val restClient: RestClient) :
    LoginDataSource.Remote {

    override suspend fun authenticate(loginRequest: LoginRequest): _Result<Unit> {
        return restClient.getRemoteCaller().login(
            loginRequest.operationType,
            loginRequest.terminal,
            loginRequest.badgeId,
            loginRequest.password,
            loginRequest.imei
        )
    }

    override suspend fun setAdminConfiguration(imei: String): AdminOptionsResult {
        return restClient.getRemoteCaller().setAdminConfiguration(imei)
    }
}