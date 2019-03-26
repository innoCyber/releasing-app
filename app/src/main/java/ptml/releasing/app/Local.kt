package ptml.releasing.app

import ptml.releasing.admin_configuration.models.AdminConfigResponse

interface Local {
    fun saveConfig(response: AdminConfigResponse?)
    fun getConfig(): AdminConfigResponse?

}