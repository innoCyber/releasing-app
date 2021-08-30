package ptml.releasing.app.data

import android.net.Uri
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import ptml.releasing.BuildConfig
import ptml.releasing.adminlogin.model.User
import ptml.releasing.app.local.Local
import ptml.releasing.app.remote.Remote
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.FileUtils
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.Damage
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.images.model.Image
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemarkResponse
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
open class ReleasingRepository @Inject constructor(
    var remote: Remote,
    var local: Local,
    var appCoroutineDispatchers: AppCoroutineDispatchers,
    private val fileUtils: FileUtils
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
            val remoteResponse = remote.getAdminConfigurationAsync(imei)
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


    override fun getSelectedConfigAsync(): Configuration {
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
        return local.getDeviceConfiguration()
            ?.toDeferredAsync() as Deferred<ConfigureDeviceResponse>
    }


    override suspend fun findCargo(
        cargoTypeId: Int?,
        operationStepId: Int?,
        terminal: Int?,
        shippingLine: String?,
        voyage: Int?,
        imei: String,
        cargoNumber: String
    ) = remote.findCargo(cargoTypeId, operationStepId, terminal, shippingLine, voyage,  imei, cargoNumber)


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

    override fun getPrinterSettings() = local.getPrinterSettings()

    override fun savePrinterSettings(settings: Settings?) =
        local.savePrinterSettings(settings)


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


    override fun setDamagesCurrentVersion(currentVersion: Long) =
        local.setDamagesCurrentVersion(currentVersion)

    override fun setVoyagesCurrentVersion(currentVersion: Long) =
        local.setVoyageCurrentVersion(currentVersion)

    override fun setQuickCurrentVersion(currentVersion: Long) =
        local.setQuickCurrentVersion(currentVersion)

    override fun setMustUpdateApp(shouldUpdate: Boolean) = local.setMustUpdateApp(shouldUpdate)

    override fun mustUpdateApp(): Boolean = local.mustUpdateApp()


    override fun setAppMinimumVersion(version: Long) = local.setAppVersion(version)

    override fun getAppMinimumVersion(): Long = local.getAppVersion()

    override fun checkToResetLocalAppUpdateValues() {
        val currentVersion = BuildConfig.VERSION_CODE.toLong()
        if (currentVersion > local.getAppVersion()) {
            local.setMustUpdateApp(false)
            local.setAppVersion(currentVersion)
        }
    }

    override fun isInternetErrorLoggingEnabled() = local.isInternetErrorLoggingEnabled()

    override fun setInternetErrorLoggingEnabled(enabled: Boolean) =
        local.setInternetErrorLoggingEnabled(enabled)

    override suspend fun addImage(cargoCode: String, image: Image) {
        local.addImage(cargoCode, image)
    }

    override suspend fun removeImage(cargoCode: String, image: Image) {
        local.removeImage(cargoCode, image)
    }

    override suspend fun getImages(cargoCode: String): Map<String, Image> {
        /*   val files =
               fileUtils.provideImageFiles(File(getRootPathCompressed(cargoCode)))
                   .map { createImage(it) }
                   .map { (it.name ?: "") to it }.toMap()
           local.storeImages(cargoCode, files)*/
        val localImages = local.getImages(cargoCode)
        Timber.d("LOCal Images: $localImages")
        //TODO: Fetch remotely and merge
        return localImages
    }

    override suspend fun storeImages(cargoCode: String, imageMap: Map<String, Image>) =
        local.storeImages(cargoCode, imageMap)

    override fun createImageFile(cargoCode: String): File {
        return fileUtils.createImageFile(cargoCode)
    }

    override fun createImage(imageFile: File): Image {
        return Image(fileUtils.getFileUri(imageFile), fileUtils.getFileName(imageFile))
    }

    override fun getRootPath(cargoCode: String?): String {
        return fileUtils.getRootPath(cargoCode)
    }

    override fun getRootPathCompressed(cargoCode: String?): String {
        return fileUtils.getRootPathCompressed(cargoCode)
    }

    override suspend fun delete(
        imageList: List<Image>,
        cargoCode: String?
    ) {
        imageList.forEach {
            val deleted = fileUtils.deleteFile(File(Uri.parse(it.imageUri).path ?: ""))
            if (deleted) {
                removeImage(cargoCode ?: "", it)
                Timber.d("File deleted successfully, remove locally from prefs")
            }
        }
    }

    override suspend fun compressImageFile(currentPhotoPath: String?, cargoCode: String?) {
        val compressedFile = fileUtils.compressFile(File(currentPhotoPath ?: ""), cargoCode)
        if (compressedFile != null) {
            //delete the uncompressed file
            fileUtils.deleteFile(File(currentPhotoPath ?: return))
        }
    }

    override fun addWorkerId(cargoCode: String, workerId: String) =
        local.addWorkerId(cargoCode, workerId)

    override fun getWorkerId(cargoCode: String): String? = local.getWorkerId(cargoCode)
}



