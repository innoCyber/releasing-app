package ptml.releasing.app.data

import ptml.releasing.app.Local
import ptml.releasing.admin_configuration.models.ConfigurationResponse

class FakeReleasingDb : Local {
    private  var configurationResponse: ConfigurationResponse? = null

    override fun saveConfig(response: ConfigurationResponse?) {
        this.configurationResponse = response
    }

    override fun getConfig(): ConfigurationResponse? {
        return configurationResponse
    }




}
