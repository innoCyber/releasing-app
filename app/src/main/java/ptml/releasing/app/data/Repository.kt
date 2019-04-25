package ptml.releasing.app.data

import kotlinx.coroutines.Deferred
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.login.model.User
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.download_damages.model.Damage
import ptml.releasing.printer.model.Settings

interface Repository {

    suspend fun verifyDeviceIdAsync(imei: String): Deferred<BaseResponse>

    suspend fun loginAsync(user: User): Deferred<BaseResponse>

    suspend fun getAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse>
    suspend fun downloadAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse>

    suspend fun downloadDamagesAsync(imei: String): Deferred<DamageResponse>
    suspend fun getDamagesAsync(imei: String): Deferred<DamageResponse>

    suspend fun setConfigurationDeviceAsync(
        cargoTypeId: Int,
        operationStepId: Int,
        terminal: Int,
        imei: String
    ): Deferred<ConfigureDeviceResponse>

    suspend fun getFormConfigAsync(): Deferred<ConfigureDeviceResponse>


    suspend fun findCargo(
            cargoTypeId: Int,
            operationStepId: Int,
            terminal: Int,
            imei: String,
            cargoNumber:String):Deferred<FindCargoResponse>

     fun getSavedConfigAsync(): Configuration
     fun setSavedConfigAsync(configuration: Configuration)

     fun isFirstAsync(): Boolean
     fun setFirst(value: Boolean)
     fun isConfiguredAsync(): Boolean
     fun setConfigured(isConfigured: Boolean)

    suspend  fun getDamagesByPosition(imei: String, position:String) : List<Damage>


    fun saveSettings(settings: Settings?)
    fun getSettings(): Settings

    fun saveOperatorName(name:String?)
    fun getOperatorName():String?


}