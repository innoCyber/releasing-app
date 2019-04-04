package ptml.releasing.app.prefs

import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.damages.model.DamageResponse

interface Prefs {
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
    fun saveConfig(response: AdminConfigResponse?)
    fun getConfig(): AdminConfigResponse?
    fun saveDamages(response: DamageResponse?)
    fun getDamages():DamageResponse?

    fun getSavedConfig():Configuration
    fun setSavedConfig(configuration: Configuration)
    fun isConfigured():Boolean
    fun setConfigured(isConfigured:Boolean)
}