package ptml.releasing.app.di.modules.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ptml.releasing.admin_config.view.AdminConfigActivity
import ptml.releasing.app.utils.remoteconfig.UpdateIntentService
import ptml.releasing.app.utils.upload.CancelWorkReceiver
import ptml.releasing.barcode_scan.BarcodeScanActivity
import ptml.releasing.cargo_info.view.CargoInfoActivity
import ptml.releasing.cargo_search.view.SearchActivity
import ptml.releasing.configuration.view.ConfigActivity
import ptml.releasing.damages.view.*
import ptml.releasing.device_configuration.view.DeviceConfigActivity
import ptml.releasing.download_damages.view.DamageActivity
import ptml.releasing.images.upload.UploadImagesActivity
import ptml.releasing.images.viewer.ImageViewerActivity
import ptml.releasing.internet_error_logs.view.ErrorLogsActivity
import ptml.releasing.printer.view.PrinterSettingsActivity
import ptml.releasing.quick_remarks.view.QuickRemarkActivity
import ptml.releasing.resetpassword.view.ResetPasswordActivity
import ptml.releasing.voyage.view.VoyageActivity


@Module
abstract class UiModule {

    @ContributesAndroidInjector()
    abstract fun bindSetupActivity(): DeviceConfigActivity


    @ContributesAndroidInjector()
    abstract fun loginActivity(): ptml.releasing.adminlogin.view.LoginActivity


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

    @ContributesAndroidInjector
    abstract fun normalLoginActivity(): ptml.releasing.login.view.LoginActivity


    @ContributesAndroidInjector
    abstract fun resetPasswordActivity(): ResetPasswordActivity

    @ContributesAndroidInjector
    abstract fun voyagesActivity(): VoyageActivity

    @ContributesAndroidInjector
    abstract fun updateImagesActivity() : UploadImagesActivity


    @ContributesAndroidInjector
    abstract fun updateImageViewerActivity() : ImageViewerActivity

    @ContributesAndroidInjector
    abstract fun cancelReceiver() : CancelWorkReceiver

}