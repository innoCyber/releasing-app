package ptml.releasing.app.di.modules.auth

import dagger.Module
import dagger.Provides
import io.mockk.mockk
import ptml.releasing.app.data.domain.repository.LoginRepository
import ptml.releasing.app.data.domain.repository.ResetPasswordRepository
import ptml.releasing.login.model.LoginDataSource
import ptml.releasing.resetpassword.model.ResetPasswordDataSource

/**
 * Created by kryptkode on 5/22/2020.
 */
@Module
class TestAuthModule {
    @Provides
    fun loginRepo(): LoginRepository {
        return mockk()
    }

    @Provides
    fun loginLocal(): LoginDataSource.Local {
        return mockk()
    }

    @Provides
    fun loginRemote(): LoginDataSource.Remote {
        return mockk()
    }


    @Provides
    fun bindRemote(): ResetPasswordDataSource.Remote {
        return mockk()
    }


    @Provides
    fun bindRepo(): ResetPasswordRepository {
        return mockk()
    }
}