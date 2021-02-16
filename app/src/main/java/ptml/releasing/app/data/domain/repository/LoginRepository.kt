package ptml.releasing.app.data.domain.repository

import ptml.releasing.app.data.domain.model.ApiResult
import ptml.releasing.app.data.domain.model.login.LoginEntity

/**
 * Created by kryptkode on 10/23/2019.
 */

interface LoginRepository {
    suspend fun authenticate(
        badgeId: String,
        password: String,
        imei: String
    ): ApiResult

    suspend fun updateAppVersion(): ApiResult

    suspend fun getLoginData(): LoginEntity
    suspend fun setLoggedIn(value: Boolean)
    suspend fun getLoggedIn(): Boolean
    suspend fun logOut(): Boolean
}