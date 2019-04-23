package ptml.releasing.app.di.modules.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ptml.releasing.admin_config.view.AdminConfigActivity
import ptml.releasing.configuration.view.ConfigActivity
import ptml.releasing.auth.view.LoginActivity
import ptml.releasing.download_damages.view.DamageActivity
import ptml.releasing.device_configuration.view.DeviceConfigActivity
import ptml.releasing.cargo_info.view.CargoInfoActivity
import ptml.releasing.cargo_search.view.BarcodeScanActivity
import ptml.releasing.home.view.HomeActivity
import ptml.releasing.cargo_search.view.SearchActivity
import ptml.releasing.damages.view.*
import ptml.releasing.printer.view.PrinterSettingsActivity


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

    @ContributesAndroidInjector()
    abstract fun findCargoActivity(): CargoInfoActivity


    @ContributesAndroidInjector()
    abstract fun damagesActivity(): DamagesActivity

    @ContributesAndroidInjector()
    abstract fun selectDamage(): ReleasingDamagesSelectDamageActivity


    @ContributesAndroidInjector()
    abstract fun selectFront(): ReleasingDamagesSelectFrontActivity


    @ContributesAndroidInjector()
    abstract fun selectSide(): ReleasingDamagesSelectSideActivity

    @ContributesAndroidInjector()
    abstract fun selectZone(): ReleasingDamagesSelectZoneActivity


    @ContributesAndroidInjector()
    abstract fun printer(): PrinterSettingsActivity


    @ContributesAndroidInjector()
    abstract fun scan(): BarcodeScanActivity



}