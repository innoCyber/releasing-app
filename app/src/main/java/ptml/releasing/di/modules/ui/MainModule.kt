package ptml.releasing.di.modules.ui

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import ptml.releasing.api.ReleasingRemote
import ptml.releasing.api.Remote
import ptml.releasing.data.ReleasingRepository
import ptml.releasing.data.Repository
import ptml.releasing.db.AppDatabase
import ptml.releasing.db.Local
import ptml.releasing.db.ReleasingLocal
import ptml.releasing.di.modules.rx.OBSERVER_ON
import ptml.releasing.di.modules.rx.SUBSCRIBER_ON
import ptml.releasing.di.scopes.ReleasingAppScope
import retrofit2.Retrofit
import javax.inject.Named

@Module()
abstract class MainModule {

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
    fun provideLocal(appDatabase: AppDatabase, @Named(SUBSCRIBER_ON) subscriberOn: Scheduler,
                     @Named(OBSERVER_ON) observerOn: Scheduler): Local {
        return ReleasingLocal(appDatabase, subscriberOn, observerOn)
    }


}