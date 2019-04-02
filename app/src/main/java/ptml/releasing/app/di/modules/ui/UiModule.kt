package ptml.releasing.app.di.modules.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ptml.releasing.app.ui.MainActivity
import ptml.releasing.admin_configuration.view.AdminConfigActivity
import ptml.releasing.auth.view.LoginActivity
import ptml.releasing.device_configuration.view.DeviceConfigActivity


@Module
abstract class UiModule {


    @ContributesAndroidInjector()
    abstract fun bindMainActivity(): MainActivity


    @ContributesAndroidInjector()
    abstract fun bindSetupActivity(): DeviceConfigActivity


    @ContributesAndroidInjector()
    abstract fun  loginActivity(): LoginActivity


    @ContributesAndroidInjector()
    abstract fun  configActivity(): AdminConfigActivity






}