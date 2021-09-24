package ptml.releasing.app.data

import kotlinx.coroutines.Deferred
import ptml.releasing.adminlogin.model.User
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.Damage
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.images.model.Image
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemarkResponse
import java.io.File

interface Repository {

    suspend fun verifyDeviceIdAsync(imei: String): Deferred<BaseResponse>

    suspend fun loginAsync(endpoint: String, authentication: String, user: User): Deferred<BaseResponse>

    suspend fun getAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse>
    suspend fun downloadAdminConfigurationAsync(imei: String): Deferred<AdminConfigResponse>

    suspend fun downloadDamagesAsync(imei: String): Deferred<DamageResponse>?
    suspend fun getDamagesAsync(imei: String): Deferred<DamageResponse>?

    suspend fun setConfigurationDeviceAsync(
        cargoTypeId: Int?,
        operationStepId: Int?,
        terminal: Int?,
        imei: String,
        id_voyage: Int
    ): Deferred<ConfigureDeviceResponse>

    suspend fun getFormConfigAsync(): Deferred<ConfigureDeviceResponse>

    suspend fun findCargo(
        cargoTypeId: String?,
        operationStepId: Int?,
        terminal: Int?,
        shippingLine: String?,
        voyage: Int?,
        imei: String,
        cargoNumber: String,
        id_voyage: Int
    ): Deferred<FindCargoResponse?>?

    fun getSelectedConfigAsync(): Configuration
    fun setSavedConfigAsync(configuration: Configuration)

    fun isFirst(): Boolean
    fun setFirst(value: Boolean)
    fun isConfiguredAsync(): Boolean
    fun setConfigured(isConfigured: Boolean)

    suspend fun getDamagesByPosition(
        imei: String,
        position: String,
        typeContainer: Int?
    ): List<Damage>


    fun savePrinterSettings(settings: Settings?)
    fun getPrinterSettings(): Settings

    fun saveOperatorName(name: String?)
    fun getOperatorName(): String?

    fun saveServerUrl(url: String?)
    fun getServerUrl(): String?


    suspend fun uploadData(
        request: FormSubmissionRequest
    ): Deferred<BaseResponse>

    suspend fun downloadQuickRemarkAsync(imei: String): Deferred<QuickRemarkResponse>?
    suspend fun getQuickRemarkAsync(imei: String): Deferred<QuickRemarkResponse>?

    fun setMustUpdateApp(shouldUpdate:Boolean)
    fun mustUpdateApp():Boolean

    fun setDamagesCurrentVersion(currentVersion:Long)
    fun setVoyagesCurrentVersion(currentVersion: Long)

    fun setQuickCurrentVersion(currentVersion:Long)

    fun checkToResetLocalAppUpdateValues()

    fun setAppMinimumVersion(version:Long)
    fun getAppMinimumVersion():Long

    fun isInternetErrorLoggingEnabled():Boolean
    fun setInternetErrorLoggingEnabled(enabled:Boolean)

    suspend fun storeImages(cargoCode: String, imageMap: Map<String, Image>)
    suspend fun addImage(cargoCode: String, image: Image)
    suspend fun removeImage(cargoCode: String, image: Image)
    suspend fun getImages(cargoCode: String): Map<String, Image>

    fun createImageFile(cargoCode: String): File
    fun createImage(imageFile: File): Image

    fun getRootPath(cargoCode: String?):String
    fun getRootPathCompressed(cargoCode: String?):String
    suspend fun delete(
        imageList: List<Image>,
        cargoCode: String?
    )

   suspend fun compressImageFile(currentPhotoPath: String?, cargoCode: String?)

    fun addWorkerId(cargoCode: String, workerId:String)
    fun getWorkerId(cargoCode: String): String?

}