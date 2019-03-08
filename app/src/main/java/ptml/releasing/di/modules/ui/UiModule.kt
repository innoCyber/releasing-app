package ptml.releasing.di.modules.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ptml.releasing.ui.MainActivity
import ptml.releasing.di.modules.ui.MainModule
import ptml.releasing.ui.DeviceConfigErrorActivity
import ptml.releasing.ui.setup.SetupActivity


@Module
abstract class UiModule {


    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity(): MainActivity


    @ContributesAndroidInjector()
    abstract fun bindSetupActivity(): SetupActivity

    @ContributesAndroidInjector
    abstract fun  bindDeviceError():DeviceConfigErrorActivity






}