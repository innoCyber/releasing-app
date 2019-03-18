package ptml.releasing.app.di.component

import dagger.Component
import ptml.releasing.app.base.BaseApiTest
import ptml.releasing.app.di.modules.local.DbModule
import ptml.releasing.app.di.modules.network.TestNetworkModule
import ptml.releasing.app.di.modules.ui.TestMainModule

import ptml.releasing.app.di.modules.ui.UiModule
import ptml.releasing.app.di.modules.viewmodel.ViewModelFactoryModule
import ptml.releasing.app.di.modules.viewmodel.ViewModelModule
import ptml.releasing.app.di.rx.TestRxJavaModule
import ptml.releasing.app.di.scopes.ReleasingAppScope

@ReleasingAppScope
@Component(modules = [TestNetworkModule::class,
    DbModule::class, ViewModelFactoryModule::class,
    ViewModelModule::class, UiModule::class, TestRxJavaModule::class, TestMainModule::class])
interface TestAppComponent {
    fun inject(baseApiTest: BaseApiTest)
}