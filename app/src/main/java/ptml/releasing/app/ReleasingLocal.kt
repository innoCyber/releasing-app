package ptml.releasing.app

import ptml.releasing.admin_configuration.models.ConfigurationResponse
import ptml.releasing.app.prefs.Prefs
import javax.inject.Inject

class ReleasingLocal @Inject constructor(var prefs: Prefs) : Local {
    override fun saveConfig(response: ConfigurationResponse?) = prefs.saveConfig(response)
    override fun getConfig() = prefs.getConfig()
}