package ptml.releasing.app.data

import kotlinx.coroutines.*
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.app.local.Local
import ptml.releasing.app.remote.Remote
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.login.model.User
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.Damage
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.printer.model.Settings
import timber.log.Timber
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
open class ReleasingRepository @Inject constructor(
        var remote: Remote,
        var local: Local,
        var appCoroutineDispatchers: AppCoroutineDispatchers
) : Repository {

    override suspend fun verifyDeviceIdAsync(imei: String) = remote.verifyDeviceIdAsync(imei)

    override suspend fun loginAsync(user: User) = remote.loginAsync(user.username, user.password)

    override suspend fun getAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse> {
        return withContext(appCoroutineDispatchers.db) {
            try {
                val localResponse = local.getConfig()
                localResponse?.toDeferredAsync() as Deferred<AdminConfigResponse>
            } catch (e: Exception) {
                Timber.e(e)
                downloadAdminConfigurationAsync(imei)
            }

        }
    }

    override suspend fun downloadAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse> {
        return withContext(appCoroutineDispatchers.network) {
            val remoteResponse = remote.setAdminConfigurationAsync(imei)
            withContext(appCoroutineDispatchers.db) {
                local.saveConfig(remoteResponse.await())
            }
            remoteResponse
        }
    }


    override suspend fun getDamagesAsync(imei: String): Deferred<DamageResponse> {
        return withContext(appCoroutineDispatchers.db) {
            try {
                val localResponse = local.getDamages()
                localResponse?.toDeferredAsync() as Deferred<DamageResponse>
            } catch (e: Exception) {
                Timber.e(e)
                downloadDamagesAsync(imei)

            }
        }
    }

    override suspend fun downloadDamagesAsync(imei: String): Deferred<DamageResponse> {
        return withContext(appCoroutineDispatchers.network) {
            val remoteResponse = remote.downloadDamagesAsync(imei)
            withContext(appCoroutineDispatchers.db) {
                local.saveDamages(remoteResponse.await())
            }
            remoteResponse
        }

    }


    override fun getSavedConfigAsync(): Configuration {
        return local.getSavedConfig()

    }

    override fun setSavedConfigAsync(configuration: Configuration) {
        local.setSavedConfig(configuration)
    }

    override fun isFirstAsync(): Boolean {
        return local.isFirst()
    }

    override fun setFirst(value: Boolean) {
        local.setFirst(value)
    }

    override fun isConfiguredAsync(): Boolean {
        return local.isConfigured()
    }

    override fun setConfigured(isConfigured: Boolean) {

        local.setConfigured(isConfigured)
    }

    override suspend fun setConfigurationDeviceAsync(
            cargoTypeId: Int?,
            operationStepId: Int?,
            terminal: Int?,
            imei: String
    ): Deferred<ConfigureDeviceResponse> {
        return withContext(appCoroutineDispatchers.network) {
            val remoteResponse = remote.setConfigurationDeviceAsync(cargoTypeId, operationStepId, terminal, imei)
            Timber.d("Gotten response: %s", remoteResponse)
            withContext(appCoroutineDispatchers.db) {
                local.saveDeviceConfiguration(remoteResponse.await())
                Timber.d("Saved response")
            }
            remoteResponse
        }
    }

    override suspend fun getFormConfigAsync(): Deferred<ConfigureDeviceResponse> {
        return local.getDeviceConfiguration()?.toDeferredAsync() as Deferred<ConfigureDeviceResponse>
    }


    override suspend fun findCargo(cargoTypeId: Int?,
                                   operationStepId: Int?,
                                   terminal: Int?,
                                   imei: String,
                                   cargoNumber:String
    ) = remote.findCargo(cargoTypeId, operationStepId, terminal, imei, cargoNumber)


    override suspend fun getDamagesByPosition(imei:String, position: String): List<Damage> {
        return withContext(appCoroutineDispatchers.db){
            val filteredList = mutableListOf<Damage>()
            val damageResponse = getDamagesAsync(imei).await()
            val list = damageResponse.data
            for (damage in list) {
                if(damage.position == position){
                    filteredList.add(damage)
                }
            }
            Timber.d("Filtered List: %s", filteredList)
            Timber.d("Filtered Size: %s", filteredList.size)
            filteredList
        }
    }

    override fun getSettings() = local.getSettings()

    override fun saveSettings(settings: Settings?) = local.saveSettings(settings)

    override fun getOperatorName() = local.getOperatorName()

    override fun saveOperatorName(name: String?) = local.saveOperatorName(name)

    override fun getServerUrl(): String? = local.getServerUrl()

    override fun saveServerUrl(url: String?)  = local.saveServerUrl(url)

}



