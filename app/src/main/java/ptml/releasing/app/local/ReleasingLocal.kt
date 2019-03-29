package ptml.releasing.app.local

import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.admin_configuration.models.Configuration
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.damages.model.DamageResponse
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
}