package ptml.releasing.resetpassword.model

import dagger.Binds
import dagger.Module
import ptml.releasing.app.data.domain.repository.ResetPasswordRepository

/**
 * Created by kryptkode on 1/21/2020.
 */

@Module
interface ResetPasswordModule {


    @Binds
    fun bindRemote(remoteDataSource: ResetPasswordRemoteDataSource): ResetPasswordDataSource.Remote


    @Binds
    fun bindRepo(resetPasswordRepositoryImpl: ResetPasswordRepositoryImpl): ResetPasswordRepository
}