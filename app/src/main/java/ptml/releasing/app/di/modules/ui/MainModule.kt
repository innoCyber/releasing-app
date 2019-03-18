package ptml.releasing.app.di.modules.ui

import dagger.Module
import dagger.Provides
import ptml.releasing.api.ReleasingRemote
import ptml.releasing.api.Remote
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.app.data.Repository
import ptml.releasing.app.Local
import ptml.releasing.app.ReleasingLocal
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.app.di.scopes.ReleasingAppScope
import retrofit2.Retrofit

@Module()
class MainModule {

    @Provides
    @ReleasingAppScope
    fun provideRepository(remote: Remote, local: Local): Repository {
        return ReleasingRepository(remote, local)
    }

    @Provides
    @ReleasingAppScope
    fun provideRemote(retrofit: Retrofit): Remote {
        return ReleasingRemote(retrofit)
    }

    @Provides
    @ReleasingAppScope
    fun provideLocal(prefs: Prefs): Local {
        return ReleasingLocal(prefs)
    }


}