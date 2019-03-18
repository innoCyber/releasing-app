package ptml.releasing.app.di.rx

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import ptml.releasing.app.di.modules.rx.OBSERVER_ON
import ptml.releasing.app.di.modules.rx.SUBSCRIBER_ON
import ptml.releasing.app.di.scopes.ReleasingAppScope
import javax.inject.Named

@Module
class TestRxJavaModule {

    @Provides
    @Named(SUBSCRIBER_ON)
    @ReleasingAppScope
    fun provideSubscriberOn(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @Named(OBSERVER_ON)
    @ReleasingAppScope
    fun provideObserverOn(): Scheduler = AndroidSchedulers.mainThread()
}
