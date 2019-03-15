package ptml.releasing.db

import ptml.releasing.db.models.config.ConfigurationResponse

interface Local {
    fun saveConfig(response: ConfigurationResponse?)
    fun getConfig():ConfigurationResponse?

}