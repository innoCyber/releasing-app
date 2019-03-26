package ptml.releasing.app

import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.damages.model.DamageResponse
import javax.inject.Inject

class ReleasingLocal @Inject constructor(var prefs: Prefs) : Local {
    override fun saveConfig(response: AdminConfigResponse?) = prefs.saveConfig(response)
    override fun getConfig() = prefs.getConfig()
    override fun getDamages() = prefs.getDamages()
    override fun saveDamages(response: DamageResponse?) = prefs.saveDamages(response)
}