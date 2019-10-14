package ptml.releasing.app.prefs

import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemarkResponse

interface Prefs {
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
    fun saveConfig(response: AdminConfigResponse?)
    fun getConfig(): AdminConfigResponse?
    fun saveDamages(response: DamageResponse?)
    fun getDamages():DamageResponse?

    fun getSavedConfig():Configuration
    fun setSavedConfig(configuration: Configuration)
    fun isConfigured():Boolean
    fun setConfigured(isConfigured:Boolean)

    fun getDeviceConfiguration(): ConfigureDeviceResponse?
    fun saveDeviceConfiguration(response: ConfigureDeviceResponse?)
    fun saveSettings(settings:Settings?)
    fun getSettings():Settings

    fun saveOperatorName(name:String?)
    fun getOperatorName():String?

    fun saveServerUrl(url:String?)
    fun getServerUrl():String?

    fun saveQuickRemarks(response: QuickRemarkResponse?)
    fun getQuickRemarks():QuickRemarkResponse?

    fun setDamagesCurrentVersion(currentVersion:Long)
    fun getDamagesCurrentVersion():Long

    fun setQuickCurrentVersion(currentVersion:Long)
    fun getQuickCurrentVersion():Long

    fun setAppVersion(version:Long)
    fun getAppVersion():Long


    fun setUpdateApp(shouldUpdate:Boolean)
    fun mustUpdateApp():Boolean

    fun setImei(imei:String)
    fun getImei():String?
}