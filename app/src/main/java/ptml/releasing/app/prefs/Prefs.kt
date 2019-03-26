package ptml.releasing.app.prefs

import ptml.releasing.admin_configuration.models.AdminConfigResponse

interface Prefs {
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
    fun saveConfig(response: AdminConfigResponse?)
    fun getConfig(): AdminConfigResponse?
}