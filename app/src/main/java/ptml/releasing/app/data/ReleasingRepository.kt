package ptml.releasing.app.data

import ptml.releasing.app.Local
import ptml.releasing.app.remote.Remote
import ptml.releasing.auth.model.User
import javax.inject.Inject

open class ReleasingRepository @Inject constructor(var remote: Remote, var local: Local) : Repository {

    override suspend fun verifyDeviceIdAsync(imei: String) = remote.verifyDeviceIdAsync(imei)

    override suspend fun loginAsync(user: User) = remote.loginAsync(user.username, user.password)

    override suspend fun getAdminConfigurationAsync(imei: String) = remote.setAdminConfigurationAsync(imei)

    override suspend fun downloadDamagesAsync(imei: String) = remote.downloadDamagesAsync(imei)
}