package ptml.releasing.app.di.components



import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.di.modules.AppModule
import ptml.releasing.app.di.modules.network.NetworkModule
import ptml.releasing.app.di.modules.worker.SampleWorkerFactory
import ptml.releasing.app.di.scopes.ReleasingAppScope

@ReleasingAppScope
@Component(modules = [AndroidInjectionModule::class, AndroidSupportInjectionModule::class, AppModule::class])
interface AppComponent: AndroidInjector<DaggerApplication> {
    fun inject(application: ReleasingApplication)
    fun inject(baseViewModel: BaseViewModel)
    fun workerFactory(): SampleWorkerFactory

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun bindApplication(application: ReleasingApplication): Builder

        @BindsInstance
        fun bindNetwork(networkModule: NetworkModule):Builder

        fun build(): AppComponent
    }
}