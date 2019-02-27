package it.ounet.releasing.di.components



import it.ounet.releasing.di.modules.ReleasingAppModule
import it.ounet.releasing.di.modules.ReleasingBuilderModule
import it.ounet.releasing.di.scopes.ReleasingAppScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import it.ounet.releasing.app.App

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