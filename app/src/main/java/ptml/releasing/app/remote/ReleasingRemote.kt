package ptml.releasing.app.remote

import ptml.releasing.admin_configuration.models.api.AdminConfigApiService
import ptml.releasing.auth.model.api.LoginApiService
import ptml.releasing.device_configuration.model.api.DeviceConfigApiService
import retrofit2.Retrofit
import javax.inject.Inject

class ReleasingRemote @Inject constructor(retrofit: Retrofit) : Remote {

    private val deviceConfigService = retrofit.create(DeviceConfigApiService::class.java)
    private val loginService = retrofit.create(LoginApiService::class.java)
    private val adminConfigService = retrofit.create(AdminConfigApiService::class.java)

    override suspend fun loginAsync(username: String?, password: String?) = loginService.loginAsync(username, password)

    override suspend fun verifyDeviceIdAsync(imei: String) = deviceConfigService.verifyDeviceIdAsync(imei)

    override suspend fun setAdminConfigurationAsync(imei: String) = adminConfigService.setAdminConfigurationAsync(imei)

}
