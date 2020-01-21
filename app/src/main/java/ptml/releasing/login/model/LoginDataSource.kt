package ptml.releasing.login.model

import ptml.releasing.app.data.domain.model.login.AdminOptionsEntity
import ptml.releasing.app.data.domain.model.login.LoginEntity
import ptml.releasing.app.data.remote.request.LoginRequest
import ptml.releasing.app.data.remote.result.AdminOptionsResult
import ptml.releasing.app.data.remote.result._Result

/**
 * Created by kryptkode on 10/23/2019.
 */

interface LoginDataSource {

    interface Remote {
        suspend fun authenticate(loginRequest: LoginRequest): _Result<Unit>
        suspend fun setAdminConfiguration(imei: String): AdminOptionsResult
    }

    interface Local {
        suspend fun setLoggedIn(value: Boolean)
        suspend fun getLoggedIn(): Boolean
        suspend fun getLoginData(): LoginEntity
        suspend fun setLoginData(loginEntity: LoginEntity)
        suspend fun setAdminOptions(entity: AdminOptionsEntity)
        suspend fun getAdminOptions(): AdminOptionsEntity
        suspend fun logOut(): Boolean
    }
}
