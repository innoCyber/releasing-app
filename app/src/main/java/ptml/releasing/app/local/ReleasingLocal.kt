package ptml.releasing.app.local

import ptml.releasing.app.prefs.Prefs
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
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

    override fun getSavedConfig(): Configuration= prefs.getSavedConfig()
    override fun setSavedConfig(configuration: Configuration) = prefs.setSavedConfig(configuration)

    override fun isFirst() = prefs.isFirst()

    override fun setFirst(value: Boolean) = prefs.setFirst(value)

    override fun isConfigured() = prefs.isConfigured()

    override fun setConfigured(isConfigured: Boolean)  = prefs.setConfigured(isConfigured)

    override fun getDeviceConfiguration() = prefs.getDeviceConfiguration()
    override fun saveDeviceConfiguration(response: ConfigureDeviceResponse?)= prefs.saveDeviceConfiguration(response)
    override fun getPrinterSettings() = prefs.getPrinterSettings()
    override fun savePrinterSettings(settings: Settings?) =
        prefs.savePrinterSettings(settings)

    override fun getOperatorName() = prefs.getOperatorName()

    override fun saveOperatorName(name: String?) = prefs.saveOperatorName(name)

    override fun getServerUrl() = prefs.getServerUrl()

    override fun saveServerUrl(url: String?) = prefs.saveServerUrl(url)

    override fun getQuickRemarks() = prefs.getQuickRemarks()

    override fun saveQuickRemarks(response: QuickRemarkResponse?) = prefs.saveQuickRemarks(response)

    override fun setDamagesCurrentVersion(currentVersion: Long) = prefs.setDamagesCurrentVersion(currentVersion)

    override fun getDamagesVersion(): Long = prefs.getDamagesCurrentVersion()

    override fun getVoyageVersion(): Long {
        return prefs.getVoyageVersion()
    }

    override fun setVoyageCurrentVersion(currentVersion: Long) {
        return prefs.setVoyageCurrentVersion(currentVersion)
    }

    override fun setQuickCurrentVersion(currentVersion: Long) = prefs.setQuickCurrentVersion(currentVersion)

    override fun getQuickRemarksVersion(): Long = prefs.getQuickCurrentVersion()

    override fun setAppVersion(version: Long) = prefs.setAppVersion(version)

    override fun getAppVersion(): Long = prefs.getAppVersion()

    override fun setMustUpdateApp(shouldUpdate: Boolean)= prefs.setUpdateApp(shouldUpdate)

    override fun mustUpdateApp(): Boolean= prefs.mustUpdateApp()
    
    override fun addImage(cargoCode: String, file: Image) = prefs.addImage(cargoCode, file)

    override fun removeImage(cargoCode: String, file: Image) = prefs.removeImage(cargoCode, file)

    override fun getImages(cargoCode: String) = prefs.getImages(cargoCode)

    override fun storeImages(cargoCode: String, imageMap: Map<String, Image>) =
        prefs.storeImages(cargoCode, imageMap)

    override fun addWorkerId(cargoCode: String, workerId: String)  = prefs.addWorkerId(cargoCode, workerId)

    override fun getWorkerId(cargoCode: String): String? = prefs.getWorkerId(cargoCode)
    override fun isInternetErrorLoggingEnabled() = prefs.isInternetErrorLoggingEnabled()

    override fun setInternetErrorLoggingEnabled(enabled: Boolean) = prefs.setInternetErrorLoggingEnabled(enabled)
}