package ptml.releasing.app.di.modules

import android.content.Context
import dagger.Binds
import dagger.Module
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.di.modules.local.DbModule
import ptml.releasing.app.di.modules.network.NetworkModule
import ptml.releasing.app.di.modules.rx.RxJavaModule
import ptml.releasing.app.di.modules.ui.MainModule
import ptml.releasing.app.di.modules.ui.UiModule
import ptml.releasing.app.di.modules.viewmodel.ViewModelFactoryModule
import ptml.releasing.app.di.modules.viewmodel.ViewModelModule
import ptml.releasing.app.di.scopes.ReleasingAppScope

@Module(includes = [NetworkModule::class, DbModule::class, ViewModelFactoryModule::class, ViewModelModule::class, UiModule::class, RxJavaModule::class, MainModule::class])
abstract class AppModule {

    @Binds
    @ReleasingAppScope
    abstract fun provideApplicationContext(application: ReleasingApplication): Context

}