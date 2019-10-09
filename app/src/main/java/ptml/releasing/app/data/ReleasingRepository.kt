package ptml.releasing.app.data

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import ptml.releasing.BuildConfig
import ptml.releasing.app.local.Local
import ptml.releasing.app.remote.Remote
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.Damage
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.login.model.User
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemarkResponse
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


    override suspend fun getDamagesAsync(imei: String): Deferred<DamageResponse>? {
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

    override suspend fun downloadDamagesAsync(imei: String): Deferred<DamageResponse>? {
        return withContext(appCoroutineDispatchers.network) {
            try {
                val remoteResponse = remote.downloadDamagesAsync(imei)
                withContext(appCoroutineDispatchers.db) {
                    local.saveDamages(remoteResponse.await())
                    remoteResponse
                }
            } catch (e: Throwable) {
                Timber.e(e)
                null
            }
        }

    }


    override fun getSavedConfigAsync(): Configuration {
        return local.getSavedConfig()

    }

    override fun setSavedConfigAsync(configuration: Configuration) {
        local.setSavedConfig(configuration)
    }

    override fun isFirst(): Boolean {
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
            val remoteResponse =
                remote.setConfigurationDeviceAsync(cargoTypeId, operationStepId, terminal, imei)
            Timber.d("Gotten response: %s", remoteResponse)
            withContext(appCoroutineDispatchers.db) {
                val response = remoteResponse.await()
                local.saveDeviceConfiguration(response)
                Timber.d("Saved response: %s", response)
            }
            remoteResponse
        }
    }

    override suspend fun getFormConfigAsync(): Deferred<ConfigureDeviceResponse> {
        return local.getDeviceConfiguration()?.toDeferredAsync() as Deferred<ConfigureDeviceResponse>
    }


    override suspend fun findCargo(
        cargoTypeId: Int?,
        operationStepId: Int?,
        terminal: Int?,
        imei: String,
        cargoNumber: String
    ) = remote.findCargo(cargoTypeId, operationStepId, terminal, imei, cargoNumber)


    override suspend fun getDamagesByPosition(
        imei: String,
        position: String,
        typeContainer: Int?
    ): List<Damage> {
        return withContext(appCoroutineDispatchers.db) {
            val filteredList = mutableListOf<Damage>()
            val damageResponse = getDamagesAsync(imei)?.await()
            val list = damageResponse?.data
            for (damage in list ?: mutableListOf()) {
                Timber.d("Damage: %s", damage)
                if (typeContainer != null) {
                    if (damage.position == position && damage.typeContainer == typeContainer) {
                        filteredList.add(damage)
                    }
                } else {
                    if (damage.position == position) {
                        filteredList.add(damage)
                    }
                }
            }
            Timber.d("Filtered List: %s", filteredList)
            Timber.d("Filtered Size: %s", filteredList.size)
            filteredList
        }
    }

    override suspend fun uploadData(request: FormSubmissionRequest) = remote.uploadData(request)

    override fun getSettings() = local.getSettings()

    override fun saveSettings(settings: Settings?) = local.saveSettings(settings)

    override fun getOperatorName() = local.getOperatorName()

    override fun saveOperatorName(name: String?) = local.saveOperatorName(name)

    override fun getServerUrl(): String? = local.getServerUrl()

    override fun saveServerUrl(url: String?) = local.saveServerUrl(url)


    override suspend fun getQuickRemarkAsync(imei: String): Deferred<QuickRemarkResponse>? {
        return withContext(appCoroutineDispatchers.db) {
            try {
                val localResponse = local.getQuickRemarks()
                localResponse?.toDeferredAsync() as Deferred<QuickRemarkResponse>
            } catch (e: Exception) {
                Timber.e(e)
                downloadQuickRemarkAsync(imei)

            }
        }
    }

    override suspend fun downloadQuickRemarkAsync(imei: String): Deferred<QuickRemarkResponse>? {
        return withContext(appCoroutineDispatchers.network) {
            try {
                val remoteResponse = remote.downloadQuickRemarkAsync(imei)
                withContext(appCoroutineDispatchers.db) {
                    local.saveQuickRemarks(remoteResponse.await())
                }
                remoteResponse

            } catch (e: Throwable) {
                Timber.e(e)
                null
            }
        }
    }

    override fun setShouldUpdateApp(shouldUpdate: Boolean) = local.setShouldUpdateApp(shouldUpdate)

    override fun shouldUpdateApp(): Boolean = local.shouldUpdateApp()

    override fun setImei(imei: String) = local.setImei(imei)

    override fun getImei(): String? = local.getImei()

    override fun setDamagesCurrentVersion(currentVersion: Long) = local.setDamagesCurrentVersion(currentVersion)

    override fun setQuickCurrentVersion(currentVersion: Long) = local.setQuickCurrentVersion(currentVersion)

    override fun setMustUpdateApp(shouldUpdate: Boolean) = local.setMustUpdateApp(shouldUpdate)

    override fun mustUpdateApp(): Boolean = local.mustUpdateApp()

    override fun setAppCurrentVersion(version: Long) = local.setAppCurrentVersion(version)

    override fun getAppCurrentVersion(): Long = local.getAppCurrentVersion()

    override fun setAppMinimumVersion(version: Long) = local.setAppMinimumVersion(version)

    override fun getAppMinimumVersion(): Long = local.getAppMinimumVersion()

    override fun checkToResetLocalAppUpdateValues() {
        val currentVersion = BuildConfig.VERSION_CODE.toLong()
        if (currentVersion > local.getAppMinimumVersion()) {
            local.setMustUpdateApp(false)
            local.setAppMinimumVersion(currentVersion)
        }
        if (currentVersion > local.getAppCurrentVersion()) {
            local.setShouldUpdateApp(false)
            local.setAppCurrentVersion(currentVersion)
        }
    }
}



