package ptml.releasing.login.model

import dagger.Binds
import dagger.Module
import ptml.releasing.app.data.domain.repository.LoginRepository

/**
 * Created by kryptkode on 1/21/2020.
 */

@Module
interface LoginModule {

    @Binds
    fun loginRepo(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository

    @Binds
    fun loginLocal(loginLocalDataSource: LoginLocalDataSource): LoginDataSource.Local

    @Binds
    fun loginRemote(loginRemoteDataSource: LoginRemoteDataSource): LoginDataSource.Remote
}