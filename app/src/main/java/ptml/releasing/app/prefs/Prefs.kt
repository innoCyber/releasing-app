package ptml.releasing.app.prefs

import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.damages.model.DamageResponse

interface Prefs {
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
    fun saveConfig(response: AdminConfigResponse?)
    fun getConfig(): AdminConfigResponse?
    fun saveDamages(response: DamageResponse?)
    fun getDamages():DamageResponse?
}