package ptml.releasing.app.local

import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.printer.model.Settings
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
}