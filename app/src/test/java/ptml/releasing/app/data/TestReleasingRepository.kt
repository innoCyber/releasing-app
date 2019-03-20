package ptml.releasing.app.data

import ptml.releasing.app.Local
import ptml.releasing.app.api.Remote

class TestReleasingRepository(remote: Remote, local: Local) : ReleasingRepository(remote, local) {

    override suspend fun getAdminConfiguration(imei: String) = remote.setAdminConfiguration(imei)
}