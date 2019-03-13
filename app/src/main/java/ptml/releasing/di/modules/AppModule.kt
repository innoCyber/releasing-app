package ptml.releasing.di.modules

import android.content.Context
import dagger.Binds
import dagger.Module
import ptml.releasing.app.App
import ptml.releasing.di.modules.local.DbModule
import ptml.releasing.di.modules.network.NetworkModule
import ptml.releasing.di.modules.rx.RxJavaModule
import ptml.releasing.di.modules.ui.MainModule
import ptml.releasing.di.modules.ui.UiModule
import ptml.releasing.di.modules.viewmodel.ViewModelFactoryModule
import ptml.releasing.di.modules.viewmodel.ViewModelModule
import ptml.releasing.di.scopes.ReleasingAppScope

@Module(includes = [NetworkModule::class, DbModule::class, ViewModelFactoryModule::class, ViewModelModule::class, UiModule::class, RxJavaModule::class, MainModule::class])
abstract class AppModule {

    @Binds
    @ReleasingAppScope
    abstract fun provideApplicationContext(application: App): Context

}