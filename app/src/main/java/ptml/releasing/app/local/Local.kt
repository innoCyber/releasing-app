package ptml.releasing.app.local

import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.Damage

import ptml.releasing.download_damages.model.DamageResponse

interface Local {
    fun saveConfig(response: AdminConfigResponse?)
    fun getConfig(): AdminConfigResponse?
    fun getDamages():DamageResponse?
    fun saveDamages(response:DamageResponse?)
    fun getSavedConfig(): Configuration
    fun setSavedConfig(configuration: Configuration)
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
    fun isConfigured():Boolean
    fun setConfigured(isConfigured:Boolean)

    fun getDeviceConfiguration(): ConfigureDeviceResponse?
    fun saveDeviceConfiguration(response:ConfigureDeviceResponse?)


}