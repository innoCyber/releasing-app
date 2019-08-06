package ptml.releasing.app.local

import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.images.model.Image
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemarkResponse
import javax.inject.Inject

class ReleasingLocal @Inject constructor(var prefs: Prefs) : Local {
    override fun saveConfig(response: AdminConfigResponse?) = prefs.saveConfig(response)
    override fun getConfig() = prefs.getConfig()
    override fun getDamages() = prefs.getDamages()
    override fun saveDamages(response: DamageResponse?) = prefs.saveDamages(response)

    override fun getSavedConfig(): Configuration = prefs.getSavedConfig()
    override fun setSavedConfig(configuration: Configuration) = prefs.setSavedConfig(configuration)

    override fun isFirst() = prefs.isFirst()

    override fun setFirst(value: Boolean) = prefs.setFirst(value)

    override fun isConfigured() = prefs.isConfigured()

    override fun setConfigured(isConfigured: Boolean) = prefs.setConfigured(isConfigured)

    override fun getDeviceConfiguration() = prefs.getDeviceConfiguration()
    override fun saveDeviceConfiguration(response: ConfigureDeviceResponse?) =
        prefs.saveDeviceConfiguration(response)

    override fun getSettings() = prefs.getSettings()
    override fun saveSettings(settings: Settings?) = prefs.saveSettings(settings)

    override fun getOperatorName() = prefs.getOperatorName()

    override fun saveOperatorName(name: String?) = prefs.saveOperatorName(name)

    override fun getServerUrl() = prefs.getServerUrl()

    override fun saveServerUrl(url: String?) = prefs.saveServerUrl(url)

    override fun getQuickRemarks() = prefs.getQuickRemarks()

    override fun saveQuickRemarks(response: QuickRemarkResponse?) = prefs.saveQuickRemarks(response)

    override fun setDamagesCurrentVersion(currentVersion: Long) =
        prefs.setDamagesCurrentVersion(currentVersion)

    override fun getDamagesCurrentVersion(): Long = prefs.getDamagesCurrentVersion()

    override fun setQuickCurrentVersion(currentVersion: Long) =
        prefs.setQuickCurrentVersion(currentVersion)

    override fun getQuickCurrentVersion(): Long = prefs.getQuickCurrentVersion()

    override fun setAppMinimumVersion(version: Long) = prefs.setAppMinimumVersion(version)

    override fun getAppMinimumVersion(): Long = prefs.getAppMinimumVersion()

    override fun setShouldUpdateApp(shouldUpdate: Boolean) = prefs.setShouldUpdateApp(shouldUpdate)

    override fun shouldUpdateApp(): Boolean = prefs.shouldUpdateApp()

    override fun setImei(imei: String) = prefs.setImei(imei)

    override fun getImei(): String? = prefs.getImei()

    override fun setMustUpdateApp(shouldUpdate: Boolean) = prefs.setMustUpdateApp(shouldUpdate)

    override fun mustUpdateApp(): Boolean = prefs.mustUpdateApp()

    override fun setAppCurrentVersion(version: Long) = prefs.setAppCurrentVersion(version)

    override fun getAppCurrentVersion(): Long = prefs.getAppCurrentVersion()

    override fun addImage(cargoCode: String, file: Image) = prefs.addImage(cargoCode, file)

    override fun removeImage(cargoCode: String, file: Image) = prefs.removeImage(cargoCode, file)

    override fun getImages(cargoCode: String) = prefs.getImages(cargoCode)

    override fun storeImages(cargoCode: String, imageMap: Map<String, Image>) =
        prefs.storeImages(cargoCode, imageMap)
}