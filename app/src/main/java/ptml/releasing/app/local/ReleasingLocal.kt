package ptml.releasing.app.local

import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemarkResponse
import javax.inject.Inject

class ReleasingLocal @Inject constructor(var prefs: Prefs) : Local {
    override fun saveConfig(response: AdminConfigResponse?) = prefs.saveConfig(response)
    override fun getConfig() = prefs.getConfig()
    override fun getDamages() = prefs.getDamages()
    override fun saveDamages(response: DamageResponse?) = prefs.saveDamages(response)

    override fun getSavedConfig(): Configuration= prefs.getSavedConfig()
    override fun setSavedConfig(configuration: Configuration) = prefs.setSavedConfig(configuration)

    override fun isFirst() = prefs.isFirst()

    override fun setFirst(value: Boolean) = prefs.setFirst(value)

    override fun isConfigured() = prefs.isConfigured()

    override fun setConfigured(isConfigured: Boolean)  = prefs.setConfigured(isConfigured)

    override fun getDeviceConfiguration() = prefs.getDeviceConfiguration()
    override fun saveDeviceConfiguration(response: ConfigureDeviceResponse?)= prefs.saveDeviceConfiguration(response)
    override fun getSettings() = prefs.getSettings()
    override fun saveSettings(settings: Settings?) = prefs.saveSettings(settings)

    override fun getOperatorName() = prefs.getOperatorName()

    override fun saveOperatorName(name: String?) = prefs.saveOperatorName(name)

    override fun getServerUrl() = prefs.getServerUrl()

    override fun saveServerUrl(url: String?) = prefs.saveServerUrl(url)

    override fun getQuickRemarks() = prefs.getQuickRemarks()

    override fun saveQuickRemarks(response: QuickRemarkResponse?) = prefs.saveQuickRemarks(response)

    override fun setDamagesCurrentVersion(currentVersion: Long) = prefs.setDamagesCurrentVersion(currentVersion)

    override fun getDamagesVersion(): Long = prefs.getDamagesCurrentVersion()

    override fun setQuickCurrentVersion(currentVersion: Long) = prefs.setQuickCurrentVersion(currentVersion)

    override fun getQuickRemarksVersion(): Long = prefs.getQuickCurrentVersion()

    override fun setAppVersion(version: Long) = prefs.setAppVersion(version)

    override fun getAppVersion(): Long = prefs.getAppVersion()


    override fun setImei(imei: String) = prefs.setImei(imei)

    override fun getImei(): String? = prefs.getImei()

    override fun setMustUpdateApp(shouldUpdate: Boolean)= prefs.setUpdateApp(shouldUpdate)

    override fun mustUpdateApp(): Boolean= prefs.mustUpdateApp()



}