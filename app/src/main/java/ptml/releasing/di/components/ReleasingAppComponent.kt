package ptml.releasing.di.components



import ptml.releasing.di.modules.ReleasingAppModule
import ptml.releasing.di.modules.ReleasingBuilderModule
import ptml.releasing.di.scopes.ReleasingAppScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import ptml.releasing.app.App

@ReleasingAppScope
@Component(modules = [AndroidInjectionModule::class, AndroidSupportInjectionModule::class,
        ReleasingBuilderModule::class, ReleasingAppModule::class])
interface ReleasingAppComponent: AndroidInjector<DaggerApplication> {
    fun inject(application: App)

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun bindApplication(application: App): Builder

        fun build(): ReleasingAppComponent
    }
}