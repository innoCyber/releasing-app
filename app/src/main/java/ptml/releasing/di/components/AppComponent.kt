package ptml.releasing.di.components



import ptml.releasing.di.modules.AppModule
import ptml.releasing.di.scopes.ReleasingAppScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import ptml.releasing.app.App
import ptml.releasing.di.modules.network.NetworkModule

@ReleasingAppScope
@Component(modules = [AndroidInjectionModule::class, AndroidSupportInjectionModule::class, AppModule::class])
interface AppComponent: AndroidInjector<DaggerApplication> {
    fun inject(application: App)

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun bindApplication(application: App): Builder

        @BindsInstance
        fun bindNetwork(networkModule: NetworkModule):Builder

        fun build(): AppComponent
    }
}