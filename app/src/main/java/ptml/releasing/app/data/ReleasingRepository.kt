package ptml.releasing.app.data

import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import ptml.releasing.app.api.Remote
import ptml.releasing.app.Local
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import ptml.releasing.auth.model.User
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

open class ReleasingRepository @Inject constructor(var remote: Remote, var local: Local) : Repository {

    override suspend fun verifyDeviceId(imei: String) = remote.verifyDeviceId(imei)
    override suspend fun login(user: User) = remote.login(user.username, user.password)


    override suspend fun getAdminConfiguration(imei: String) = remote.setAdminConfiguration(imei)


}