package ptml.releasing.di.modules.ui

import dagger.Module
import dagger.Provides
import ptml.releasing.FakeReleasingDb
import ptml.releasing.api.ReleasingRemote
import ptml.releasing.api.Remote
import ptml.releasing.data.Repository
import ptml.releasing.data.TestReleasingRepository
import ptml.releasing.db.Local
import ptml.releasing.di.scopes.ReleasingAppScope
import retrofit2.Retrofit

@Module
class TestMainModule {

    @Provides
    @ReleasingAppScope
    fun provideRepository(remote: Remote, local: Local): Repository {
        return TestReleasingRepository(remote, local)
    }

    @Provides
    @ReleasingAppScope
    fun provideRemote(retrofit: Retrofit): Remote {
        return ReleasingRemote(retrofit)
    }

    @Provides
    @ReleasingAppScope
    fun provideLocal(
    ): Local {
        return FakeReleasingDb()
    }

}