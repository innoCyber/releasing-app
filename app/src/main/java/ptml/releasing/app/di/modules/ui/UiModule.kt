package ptml.releasing.app.di.modules.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ptml.releasing.admin_config.view.AdminConfigActivity
import ptml.releasing.configuration.view.ConfigActivity
import ptml.releasing.auth.view.LoginActivity
import ptml.releasing.damages.view.DamageActivity
import ptml.releasing.device_configuration.view.DeviceConfigActivity
import ptml.releasing.home.view.HomeActivity
import ptml.releasing.search.view.SearchActivity


@Module
abstract class UiModule {

    @ContributesAndroidInjector()
    abstract fun bindSetupActivity(): DeviceConfigActivity


    @ContributesAndroidInjector()
    abstract fun loginActivity(): LoginActivity


    @ContributesAndroidInjector()
    abstract fun configActivity(): ConfigActivity


    @ContributesAndroidInjector()
    abstract fun homeActivity(): HomeActivity

    @ContributesAndroidInjector()
    abstract fun damageActivity(): DamageActivity

    @ContributesAndroidInjector()
    abstract fun searchActivity(): SearchActivity

    @ContributesAndroidInjector()
    abstract fun adminConfigActivity(): AdminConfigActivity

}