package ptml.releasing.app.api

import retrofit2.Retrofit
import javax.inject.Inject

class ReleasingRemote @Inject constructor(var retrofit: Retrofit) : Remote {

    private val deviceConfigService = retrofit.create(ApiService::class.java)

    override suspend fun login(username: String?, password: String?) = deviceConfigService.login(username, password)

    override suspend fun verifyDeviceId(imei: String) = deviceConfigService.verifyDeviceId(imei)

    override suspend fun setAdminConfiguration(imei: String) = deviceConfigService.setAdminConfiguration(imei)

}
