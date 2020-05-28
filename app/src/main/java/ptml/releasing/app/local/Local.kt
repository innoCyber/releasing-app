package ptml.releasing.app.local

import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemarkResponse

interface Local {
    fun saveConfig(response: AdminConfigResponse?)
    fun getConfig(): AdminConfigResponse?
    fun getDamages():DamageResponse?
    fun saveDamages(response:DamageResponse?)
    fun getSavedConfig(): Configuration
    fun setSavedConfig(configuration: Configuration)
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
    fun isConfigured():Boolean
    fun setConfigured(isConfigured:Boolean)

    fun getDeviceConfiguration(): ConfigureDeviceResponse?
    fun saveDeviceConfiguration(response:ConfigureDeviceResponse?)

    fun savePrinterBarcodeSettings(settings: Settings?)
    fun getPrinterBarcodeSettings(): Settings

    fun getPrinterMultilineSettings(): Settings
    fun savePrinterMultilineSettings(settings: Settings?)

    fun saveOperatorName(name:String?)
    fun getOperatorName():String?

    fun saveServerUrl(url:String?)
    fun getServerUrl():String?

    fun saveQuickRemarks(response: QuickRemarkResponse?)
    fun getQuickRemarks(): QuickRemarkResponse?

    fun setDamagesCurrentVersion(currentVersion:Long)
    fun getDamagesVersion():Long

    fun setVoyageCurrentVersion(currentVersion: Long)
    fun getVoyageVersion(): Long

    fun setQuickCurrentVersion(currentVersion:Long)
    fun getQuickRemarksVersion():Long

    fun setAppVersion(version:Long)
    fun getAppVersion():Long


    fun setImei(imei:String)
    fun getImei():String?

    fun setMustUpdateApp(shouldUpdate:Boolean)
    fun mustUpdateApp():Boolean

    fun isInternetErrorLoggingEnabled():Boolean
    fun setInternetErrorLoggingEnabled(enabled:Boolean)


}