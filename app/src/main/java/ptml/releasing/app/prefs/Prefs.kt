package ptml.releasing.app.prefs

import ptml.releasing.admin_configuration.models.ConfigurationResponse

interface Prefs {
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
    fun saveConfig(response: ConfigurationResponse?)
    fun getConfig(): ConfigurationResponse?
}