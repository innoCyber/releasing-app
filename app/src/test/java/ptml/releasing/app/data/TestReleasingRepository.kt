package ptml.releasing.app.data

import io.reactivex.Observable
import ptml.releasing.api.Remote
import ptml.releasing.app.Local
import ptml.releasing.admin_configuration.models.ConfigurationResponse

class TestReleasingRepository(remote: Remote, local: Local) : ReleasingRepository(remote, local) {

    override fun getAdminConfiguration(imei: String): Observable<ConfigurationResponse> {
        return remote.setAdminConfiguration(imei)
    }

}