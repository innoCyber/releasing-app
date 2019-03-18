package ptml.releasing.app.data

import io.reactivex.Observable
import ptml.releasing.api.Remote
import ptml.releasing.app.Local
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import ptml.releasing.auth.model.User
import timber.log.Timber
import javax.inject.Inject

open class ReleasingRepository @Inject constructor(var remote: Remote, var local: Local) : Repository {

    override fun verifyDeviceId(imei: String) = remote.verifyDeviceId(imei)
    override fun login(user: User) = remote.login(user.username, user.password)


    override fun getAdminConfiguration(imei: String): Observable<ConfigurationResponse> {
        return remoteConfiguration(imei)
    }

    private fun remoteConfiguration(imei: String): Observable<ConfigurationResponse> {
        Timber.d("Getting config  from server")
        return remote.setAdminConfiguration(imei)
            .map{local.saveConfig(it)
                    it}

    }
}