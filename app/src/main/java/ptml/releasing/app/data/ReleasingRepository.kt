package ptml.releasing.app.data

import kotlinx.coroutines.*
import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.admin_configuration.models.Configuration
import ptml.releasing.app.local.Local
import ptml.releasing.app.remote.Remote
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.auth.model.User
import ptml.releasing.damages.model.DamageResponse
import timber.log.Timber
import javax.inject.Inject

open class ReleasingRepository @Inject constructor(var remote: Remote,
                                                   var local: Local,
                                                   var appCoroutineDispatchers: AppCoroutineDispatchers) : Repository {

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


    override  suspend fun getSavedConfigAsync(): Deferred<Configuration> {
        return withContext(appCoroutineDispatchers.db) {
            local.getSavedConfig().toDeferredAsync() as Deferred<Configuration>
        }
    }

    override suspend fun setSavedConfigAsync(configuration: Configuration) {
        return withContext(appCoroutineDispatchers.db) {
            local.setSavedConfig(configuration)
        }
    }

    override suspend fun isFirstAsync(): Deferred<Boolean> {
        return GlobalScope.async { local.isFirst() }
    }

    override suspend fun setFirst(value: Boolean) {
        withContext(appCoroutineDispatchers.db){
            local.setFirst(value)
        }
    }

    override suspend fun isConfiguredAsync(): Deferred<Boolean> {
        return GlobalScope.async { local.isConfigured() }
    }

    override suspend fun setConfigured(isConfigured: Boolean) {
        withContext(appCoroutineDispatchers.db){
            local.setConfigured(isConfigured)
        }
    }
}



