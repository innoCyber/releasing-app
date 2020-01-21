package ptml.releasing.app.di.modules.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ptml.releasing.admin_config.view.AdminConfigActivity
import ptml.releasing.adminlogin.view.LoginActivity
import ptml.releasing.app.utils.remoteconfig.UpdateIntentService
import ptml.releasing.barcode_scan.BarcodeScanActivity
import ptml.releasing.cargo_info.view.CargoInfoActivity
import ptml.releasing.cargo_search.view.SearchActivity
import ptml.releasing.configuration.view.ConfigActivity
import ptml.releasing.damages.view.*
import ptml.releasing.device_configuration.view.DeviceConfigActivity
import ptml.releasing.download_damages.view.DamageActivity
import ptml.releasing.internet_error_logs.view.ErrorLogsActivity
import ptml.releasing.printer.view.PrinterSettingsActivity
import ptml.releasing.quick_remarks.view.QuickRemarkActivity


@Module
abstract class UiModule {

    @ContributesAndroidInjector()
    abstract fun bindSetupActivity(): DeviceConfigActivity


    @ContributesAndroidInjector()
    abstract fun loginActivity(): LoginActivity


    @ContributesAndroidInjector()
    abstract fun configActivity(): ConfigActivity


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


    @ContributesAndroidInjector()
    abstract fun quickRemarks(): QuickRemarkActivity

    @ContributesAndroidInjector
    abstract fun updateIntentService(): UpdateIntentService

    @ContributesAndroidInjector
    abstract fun errorLogsActivity(): ErrorLogsActivity

}