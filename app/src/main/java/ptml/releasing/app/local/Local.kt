package ptml.releasing.app.local

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ptml.releasing.cargo_search.domain.model.ChassisNumber
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.images.model.Image
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemarkResponse

interface Local {
    suspend fun saveChassisNumber(chassisNumber: ChassisNumber)
    fun getChassisNumber(): LiveData<List<ChassisNumber>>
    suspend fun deleteChassisNumber(chassisNumber: String?)
    fun saveConfig(response: AdminConfigResponse?)
    fun getConfig(): AdminConfigResponse?
    fun getDamages():DamageResponse?
    fun saveDamages(response:DamageResponse?)
    fun getSavedConfig(): Configuration
    fun setSavedConfig(configuration: Configuration)
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
    fun isConfigured():Boolean
    fun setConfigured(isConfigured:Boolean)

    fun getDeviceConfiguration(): ConfigureDeviceResponse?
    fun saveDeviceConfiguration(response:ConfigureDeviceResponse?)

    fun savePrinterSettings(settings: Settings?)
    fun getPrinterSettings(): Settings


    fun saveOperatorName(name:String?)
    fun getOperatorName():String?

    fun saveServerUrl(url:String?)
    fun getServerUrl():String?

    fun saveQuickRemarks(response: QuickRemarkResponse?)
    fun getQuickRemarks(): QuickRemarkResponse?

    fun setDamagesCurrentVersion(currentVersion:Long)
    fun getDamagesVersion():Long

    fun setVoyageCurrentVersion(currentVersion: Long)
    fun getVoyageVersion(): Long

    fun setQuickCurrentVersion(currentVersion:Long)
    fun getQuickRemarksVersion():Long

    fun setAppVersion(version:Long)
    fun getAppVersion():Long

    fun setMustUpdateApp(shouldUpdate:Boolean)
    fun mustUpdateApp():Boolean

    fun storeImages(cargoCode:String, imageMap: Map<String, Image>)
    fun getImages(cargoCode: String): Map<String, Image>
    fun isInternetErrorLoggingEnabled():Boolean
    fun setInternetErrorLoggingEnabled(enabled:Boolean)


    fun addImage(cargoCode: String, file: Image)
    fun removeImage(cargoCode: String, file: Image)

    fun addWorkerId(cargoCode: String, workerId:String)
    fun getWorkerId(cargoCode: String): String?
}