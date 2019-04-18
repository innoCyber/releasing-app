package ptml.releasing.app.remote

import ptml.releasing.auth.model.api.LoginApiService
import ptml.releasing.configuration.models.api.ConfigApiService
import ptml.releasing.damages.model.api.DamageApiService
import ptml.releasing.device_configuration.model.api.DeviceConfigApiService
import ptml.releasing.cargo_search.model.api.FindCargoService
import retrofit2.Retrofit
import javax.inject.Inject

class ReleasingRemote @Inject constructor(retrofit: Retrofit) : Remote {

    private val deviceConfigService = retrofit.create(DeviceConfigApiService::class.java)
    private val loginService = retrofit.create(LoginApiService::class.java)
    private val adminConfigService = retrofit.create(ConfigApiService::class.java)
    private val damageService = retrofit.create(DamageApiService::class.java)
    private val findCargoService = retrofit.create(FindCargoService::class.java)

    override suspend fun loginAsync(username: String?, password: String?) = loginService.loginAsync(username, password)

    override suspend fun verifyDeviceIdAsync(imei: String) = deviceConfigService.verifyDeviceIdAsync(imei)

    override suspend fun setAdminConfigurationAsync(imei: String) = adminConfigService.setAdminConfigurationAsync(imei)

    override suspend fun downloadDamagesAsync(imei: String) = damageService.downloadDamagesAsync(imei)

    override suspend fun setConfigurationDeviceAsync(
            cargoTypeId: Int,
            operationStepId: Int,
            terminal: Int,
            imei: String
    ) = adminConfigService.setConfigurationDeviceAsync(operationStepId, imei)


    override suspend fun findCargo(cargoTypeId: Int,
                                   operationStepId: Int,
                                   terminal: Int,
                                   imei: String,
                                   cargoNumber:String
    ) = findCargoService.findCargo(cargoTypeId, operationStepId, terminal, imei, cargoNumber)
}

