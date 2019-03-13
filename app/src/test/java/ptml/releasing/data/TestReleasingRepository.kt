package ptml.releasing.data

import io.reactivex.Observable
import ptml.releasing.api.Remote
import ptml.releasing.db.Local
import ptml.releasing.db.models.config.response.ConfigurationResponse

class TestReleasingRepository(remote: Remote, local: Local) : ReleasingRepository(remote, local) {

    override fun getAdminConfiguration(imei: String): Observable<ConfigurationResponse> {
        return  remote.setAdminConfiguration(imei) //testing only the server
    }

}