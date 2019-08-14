package ptml.releasing.app.di.component


import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import ptml.releasing.app.TestApplication
import ptml.releasing.app.data.Repository
import ptml.releasing.app.di.components.AppComponent
import ptml.releasing.app.di.modules.AppModule
import ptml.releasing.app.di.modules.TestAppModule
import ptml.releasing.app.di.modules.worker.SampleWorkerFactory
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.images.worker.ImageUploadWorkerTest

@ReleasingAppScope
@Component(modules = [AndroidInjectionModule::class, AndroidSupportInjectionModule::class, TestAppModule::class])
interface TestComponent : AppComponent {
    fun repository(): Repository
    fun factory(): SampleWorkerFactory
    fun inject(deleteWorkerTest: ImageUploadWorkerTest)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun bindApplication(application: TestApplication): Builder


        fun build(): TestComponent
    }
}