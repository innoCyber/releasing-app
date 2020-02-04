package ptml.releasing.login.model


import ptml.releasing.app.data.domain.model.login.LoginEntity
import ptml.releasing.app.data.local.LocalDataManager
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */

class LoginLocalDataSource @Inject constructor(private val localDataManager: LocalDataManager) :
    LoginDataSource.Local {

    override suspend fun getLoginData(): LoginEntity {
        return localDataManager.getLoginData()
    }

    override suspend fun setLoginData(loginEntity: LoginEntity) {
        return localDataManager.setLoginData(loginEntity)
    }

    override suspend fun setLoggedIn(value: Boolean) {
        return localDataManager.setLoggedIn(value)
    }

    override suspend fun getLoggedIn(): Boolean {
        return localDataManager.loggedIn()
    }

    override suspend fun logOutUser(): Boolean {
        localDataManager.setLoggedIn(false)
        localDataManager.clearLoginData()
        return true
    }
}