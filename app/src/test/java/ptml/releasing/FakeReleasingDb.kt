package ptml.releasing

import ptml.releasing.db.Local
import ptml.releasing.db.models.config.ConfigurationResponse

class FakeReleasingDb : Local {
    private  var configurationResponse: ConfigurationResponse? = null

    override fun saveConfig(response: ConfigurationResponse?) {
        this.configurationResponse = response
    }

    override fun getConfig(): ConfigurationResponse? {
        return configurationResponse
    }




}
