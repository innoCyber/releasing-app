package ptml.releasing.app.remote

import kotlinx.coroutines.Deferred
import ptml.releasing.adminlogin.model.api.LoginApiService
import ptml.releasing.app.data.local.LocalDataManager
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.cargo_info.model.api.UploadDataService
import ptml.releasing.cargo_search.model.FindCargoItems
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.model.api.FindCargoService
import ptml.releasing.configuration.models.api.ConfigApiService
import ptml.releasing.device_configuration.model.api.DeviceConfigApiService
import ptml.releasing.download_damages.model.api.DamageApiService
import ptml.releasing.quick_remarks.model.api.QuickRemarkService
import retrofit2.Retrofit
import javax.inject.Inject

class ReleasingRemote @Inject constructor(
    retrofit: Retrofit,
    val localDataManager: LocalDataManager) : Remote {

    private val deviceConfigService = retrofit.create(DeviceConfigApiService::class.java)
    private val loginService = retrofit.create(LoginApiService::class.java)
    private val adminConfigService = retrofit.create(ConfigApiService::class.java)
    private val damageService = retrofit.create(DamageApiService::class.java)
    private val findCargoService = retrofit.create(FindCargoService::class.java)
    private val uploadDataService = retrofit.create(UploadDataService::class.java)
    private val quickRemarkService = retrofit.create(QuickRemarkService::class.java)

    override suspend fun loginAsync(username: String?, password: String?) =
        loginService.loginAsync(localDataManager.getStaticAuth(),username, password)

    override suspend fun verifyDeviceIdAsync(imei: String) =
        deviceConfigService.verifyDeviceIdAsync(localDataManager.getStaticAuth(), imei)

    override suspend fun getAdminConfigurationAsync(imei: String) =
        adminConfigService.getAdminConfigurationAsync(localDataManager.getStaticAuth(), imei)

    override suspend fun downloadDamagesAsync(imei: String) =
        damageService.downloadDamagesAsync(localDataManager.getStaticAuth(), imei)

    override suspend fun downloadQuickRemarkAsync(imei: String) =
        quickRemarkService.downloadQuickRemarksAsync(localDataManager.getStaticAuth(), imei)

    override suspend fun setConfigurationDeviceAsync(
        cargoTypeId: Int?,
        operationStepId: Int?,
        terminal: Int?,
        imei: String
    ) = adminConfigService.setConfigurationDeviceAsync(localDataManager.getStaticAuth(), operationStepId, imei)

    override suspend fun findCargo(
        cargoTypeId: String?,
        operationStepId: Int?,
        terminal: Int?,
        shippingLine: String?,
        voyage: Int?,
        imei: String,
        cargoNumber: String,
        id_voyage: Int
    ): Deferred<FindCargoResponse?>? {
        val findCargoItems = FindCargoItems(cargoTypeId,operationStepId,terminal,shippingLine,voyage,imei,cargoNumber,id_voyage)
        return findCargoService.findCargo(
            localDataManager.getStaticAuth(),
            findCargoItems
        )
    }

    override suspend fun uploadData(request: FormSubmissionRequest) =
        uploadDataService.uploadData(localDataManager.getStaticAuth(), request)

    override suspend fun downloadPOD(idVoyage: Int) =
        findCargoService.downloadPOD(localDataManager.getStaticAuth(),idVoyage)

}

