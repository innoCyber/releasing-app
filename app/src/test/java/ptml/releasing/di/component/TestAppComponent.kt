package ptml.releasing.di.component

import dagger.Component
import ptml.releasing.base.BaseApiTest
import ptml.releasing.di.modules.local.DbModule
import ptml.releasing.di.modules.network.TestNetworkModule

import ptml.releasing.di.modules.ui.UiModule
import ptml.releasing.di.modules.viewmodel.ViewModelFactoryModule
import ptml.releasing.di.modules.viewmodel.ViewModelModule
import ptml.releasing.di.rx.TestRxJavaModule
import ptml.releasing.di.scopes.ReleasingAppScope

@ReleasingAppScope
@Component(modules = [TestNetworkModule::class,
    DbModule::class, ViewModelFactoryModule::class,
    ViewModelModule::class, UiModule::class, TestRxJavaModule::class])
interface TestAppComponent {
    fun inject(baseApiTest: BaseApiTest)
}