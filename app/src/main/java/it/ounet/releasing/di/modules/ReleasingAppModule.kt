package it.ounet.releasing.di.modules

import android.content.Context

import it.ounet.releasing.di.modules.local.ReleasingDbModule
import it.ounet.releasing.di.modules.network.RetrofitModule
import it.ounet.releasing.di.scopes.ReleasingAppScope
import dagger.Binds
import dagger.Module
import it.ounet.releasing.app.App

@Module(includes = [RetrofitModule::class, ReleasingDbModule::class, ViewModelFactoryModule::class, ViewModelModule::class])
abstract class ReleasingAppModule {

    @Binds
    @ReleasingAppScope
    abstract fun provideApplicationContext(application: App): Context
}