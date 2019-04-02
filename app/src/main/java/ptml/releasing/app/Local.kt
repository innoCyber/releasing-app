package ptml.releasing.app

import ptml.releasing.admin_configuration.models.ConfigurationResponse

interface Local {
    fun saveConfig(response: ConfigurationResponse?)
    fun getConfig(): ConfigurationResponse?

}