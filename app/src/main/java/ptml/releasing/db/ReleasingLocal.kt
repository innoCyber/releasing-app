package ptml.releasing.db

import ptml.releasing.db.models.config.ConfigurationResponse
import ptml.releasing.db.prefs.Prefs
import javax.inject.Inject

class ReleasingLocal @Inject constructor(var prefs: Prefs) : Local {
    override fun saveConfig(response: ConfigurationResponse?) = prefs.saveConfig(response)
    override fun getConfig() = prefs.getConfig()
}