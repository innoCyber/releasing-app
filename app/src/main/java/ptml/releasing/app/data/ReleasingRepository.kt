package ptml.releasing.app.data

import kotlinx.coroutines.*
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.app.local.Local
import ptml.releasing.app.remote.Remote
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.auth.model.User
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.damages.model.DamageResponse
import timber.log.Timber
import javax.inject.Inject

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
        cargoTypeId: Int,
        operationStepId: Int,
        terminal: Int,
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
}



