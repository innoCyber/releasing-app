package ptml.releasing.db.prefs

import ptml.releasing.db.models.config.ConfigurationResponse

interface Prefs {
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
    fun saveConfig(response: ConfigurationResponse?)
    fun getConfig(): ConfigurationResponse?
}