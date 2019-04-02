package ptml.releasing.app

import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.damages.model.DamageResponse

interface Local {
    fun saveConfig(response: AdminConfigResponse?)
    fun getConfig(): AdminConfigResponse?
    fun getDamages():DamageResponse?
    fun saveDamages(response:DamageResponse?)
}