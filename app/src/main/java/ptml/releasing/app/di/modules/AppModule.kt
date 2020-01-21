package ptml.releasing.app.di.modules

import android.content.Context
import dagger.Binds
import dagger.Module
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.data.domain.repository.ImeiRepository
import ptml.releasing.app.data.local.BaseUrlProviderImpl
import ptml.releasing.app.data.local.ImeiRepositoryImpl
import ptml.releasing.app.data.local.LocalDataManager
import ptml.releasing.app.data.local.PreferencesManagerImpl
import ptml.releasing.app.data.remote.BaseUrlProvider
import ptml.releasing.app.di.modules.local.LocalModule
import ptml.releasing.app.di.modules.main.MainModule
import ptml.releasing.app.di.modules.network.NetworkModule
import ptml.releasing.app.di.modules.ui.UiModule
import ptml.releasing.app.di.modules.viewmodel.ViewModelFactoryModule
import ptml.releasing.app.di.modules.viewmodel.ViewModelModule
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.login.model.LoginModule

@Module(includes = [NetworkModule::class, LocalModule::class, ViewModelFactoryModule::class, ViewModelModule::class, UiModule::class, MainModule::class, LoginModule::class])
abstract class AppModule {

    @Binds
    @ReleasingAppScope
    abstract fun provideApplicationContext(application: ReleasingApplication): Context


    @Binds
    @ReleasingAppScope
    abstract fun imeiRepo(imeiRepositoryImpl: ImeiRepositoryImpl): ImeiRepository

    @Binds
    abstract fun baseUrl(imeiRepositoryImpl: BaseUrlProviderImpl): BaseUrlProvider

    @Binds
    @ReleasingAppScope
    abstract fun localPrefs(preferencesManagerImpl: PreferencesManagerImpl): LocalDataManager


}