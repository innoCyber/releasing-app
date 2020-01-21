package ptml.releasing.app.data.domain.repository

import ptml.releasing.app.data.domain.model.ApiResult
import ptml.releasing.app.data.domain.model.login.AdminOptionsEntity
import ptml.releasing.app.data.domain.model.login.LoginEntity
import ptml.releasing.app.data.domain.model.login.OperationStep
import ptml.releasing.app.data.domain.model.login.Terminal

/**
 * Created by kryptkode on 10/23/2019.
 */

interface LoginRepository {
    suspend fun authenticate(
        badgeId: String,
        password: String,
        imei: String,
        operationType: OperationStep,
        terminal: Terminal
    ): ApiResult

    suspend fun getLoginData(): LoginEntity
    suspend fun setAdminConfiguration(imei: String): AdminOptionsEntity
    suspend fun setLoggedIn(value: Boolean)
    suspend fun getLoggedIn(): Boolean
    suspend fun logOut(): Boolean
}