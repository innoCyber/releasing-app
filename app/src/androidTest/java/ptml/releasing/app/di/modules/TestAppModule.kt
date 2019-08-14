package ptml.releasing.app.di.modules

import android.content.Context
import dagger.Binds
import dagger.Module
import ptml.releasing.app.TestApplication
import ptml.releasing.app.di.modules.local.DbModule
import ptml.releasing.app.di.modules.main.TestMainModule
import ptml.releasing.app.di.modules.network.NetworkModule
import ptml.releasing.app.di.modules.ui.UiModule
import ptml.releasing.app.di.modules.viewmodel.ViewModelFactoryModule
import ptml.releasing.app.di.modules.viewmodel.ViewModelModule
import ptml.releasing.app.di.modules.worker.SampleAssistedInjectModule
import ptml.releasing.app.di.modules.worker.WorkerBindingModule
import ptml.releasing.app.di.scopes.ReleasingAppScope

/**
 * Created by kryptkode on 8/6/2019
 */
@Module(
    includes = [
        NetworkModule::class,
        DbModule::class,
        ViewModelFactoryModule::class,
        ViewModelModule::class,
        UiModule::class,
        TestMainModule::class,
        WorkerBindingModule::class,
        SampleAssistedInjectModule::class]
)
abstract class TestAppModule{
    @Binds
    @ReleasingAppScope
    abstract fun provideApplicationContext(application: TestApplication): Context
}