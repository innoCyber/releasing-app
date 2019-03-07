package ptml.releasing.di.modules

import android.content.Context
import dagger.Binds
import dagger.Module
import ptml.releasing.app.App
import ptml.releasing.di.modules.local.ReleasingDbModule
import ptml.releasing.di.modules.network.RetrofitModule
import ptml.releasing.di.scopes.ReleasingAppScope

@Module(includes = [RetrofitModule::class, ReleasingDbModule::class, ViewModelFactoryModule::class, ViewModelModule::class])
abstract class ReleasingAppModule {

    @Binds
    @ReleasingAppScope
    abstract fun provideApplicationContext(application: App): Context


}