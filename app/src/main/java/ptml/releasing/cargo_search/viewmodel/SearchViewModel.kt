package ptml.releasing.cargo_search.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.R
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.repository.ImeiRepository
import ptml.releasing.app.data.domain.state.DataState
import ptml.releasing.app.form.FormMappers
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.livedata.Event
import ptml.releasing.app.utils.livedata.asLiveData
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.cargo_info.model.FormDamage
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.cargo_search.domain.model.ChassisNumber
import ptml.releasing.cargo_search.model.*
import ptml.releasing.configuration.models.ReleasingOptions
import ptml.releasing.damages.view.DamagesActivity
import ptml.releasing.form.FormType
import ptml.releasing.form.utils.Constants.VOYAGE_ID
import ptml.releasing.save_time_worker.CheckLoginWorker
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

open class SearchViewModel @Inject constructor(
    private val imeiRepository: ImeiRepository,
    private val context: Context,
    private val formMappers: FormMappers,
    repository: Repository,
    dispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, dispatchers) {

    val chassisNumbers: LiveData<List<ChassisNumber>> = repository.getChassisNumber()

    private val _openAdmin = MutableLiveData<Event<Unit>>()
    val openAdMin: LiveData<Event<Unit>> = _openAdmin

    private val _verify = MutableLiveData<Event<Unit>>()
    val verify: LiveData<Event<Unit>> = _verify

    private val _scan = MutableLiveData<Event<Unit>>()
    val scan: LiveData<Event<Unit>> = _scan

    private val _openDeviceConfiguration = MutableLiveData<Event<Unit>>()
    val openDeviceConfiguration: LiveData<Event<Unit>> = _openDeviceConfiguration

    private val _openVoyage = MutableLiveData<Event<Unit>>()
    val openVoyage: LiveData<Event<Unit>> = _openVoyage


    private val _networkState = MutableLiveData<Event<NetworkState>>()
    val networkState: LiveData<Event<NetworkState>> = _networkState

    private val _cargoNumberValidation = MutableLiveData<Int>()
    val cargoNumberValidation: LiveData<Int> = _cargoNumberValidation

    private val _chassisNumber = MutableLiveData<String?>()
    val chassisNumber: LiveData<String?> = _chassisNumber

    private val _podSpinnerItems = MutableLiveData<ArrayList<ReleasingOptions>>()
    val podSpinnerItems: LiveData<ArrayList<ReleasingOptions>> = _podSpinnerItems

    private val _findCargoResponse = MutableLiveData<FindCargoResponse>()
    val findCargoResponse: LiveData<FindCargoResponse> = _findCargoResponse


    private val _findCargoHolder = MutableLiveData<FindCargoResponse>()

    private val _errorMessage = MutableLiveData<CargoNotFoundResponse>()
    val errorMessage: LiveData<CargoNotFoundResponse> = _errorMessage

    private val mutableImei = MutableLiveData<Event<String?>>()
    val imeiNumber = mutableImei.asLiveData()

    private val mutableUpdateAppVersion = MutableLiveData<DataState<Unit>>()
    val updateAppVersion = mutableUpdateAppVersion.asLiveData()

    init {
        scheduleCheckLoginWorker()
    }

    fun openAdmin() {
        _openAdmin.value = Event(Unit)

    }

    fun verify() {
        _verify.value = Event(Unit)
    }

    fun deleteChassisNumber(chassisNumber: String?) {
        viewModelScope.launch {
            repository.deleteChassisNumber(chassisNumber)
        }
    }

    fun saveChassisNumber(cargoNumber: String?) {
        viewModelScope.launch {
            repository.saveChassisNumber(ChassisNumber(0, cargoNumber))
        }
    }

//    fun downloadPOD(idVoyage: Int){
//        viewModelScope.launch {
//            try {
//                val setDownloadPODResponse = repository.downloadPOD(idVoyage)
//                if (setDownloadPODResponse.success) {
//                    _podSpinnerItems.value = setDownloadPODResponse.operation_step as ArrayList<PODOperationStep>
//                    for (items in setDownloadPODResponse.operation_step){
//                        _id_pod.value = items.ID_POD
//                    }
//                }
//            } catch (e: Throwable) {}
//        }
//    }

    fun getFormConfig(){
        viewModelScope.launch {
            val result = repository.getFormConfigAsync().await()
           // _podSpinnerItems.value = result.data as ArrayList<PODOperationStep>
            if (result.data !=null){
            for (i in result.data){
                if (i.title.toLowerCase(Locale.ROOT).contains("pod")){
                    _podSpinnerItems.value = i.options as ArrayList<ReleasingOptions>
//                    for(items in i.options){
//                        //.value = items.name
//                        Log.d("dgrgrw", "getFormConfig: ${items.name}")
//                    }
               }
            }}
        }
    }

    fun submitForm(
        findCargoResponse: FindCargoResponse?,
        cargoCode: String?,
        imei: String?
    ) {

        CoroutineScope(dispatchers.network).launch {
            try {

                val operator = loginRepository.getLoginData().badgeId

                val configuration = repository.getSelectedConfigAsync()
                val values = FormValue(value = "Ok")
                val formSubmissionRequest = FormSubmissionRequest(
                    listOf(values),
                    emptyList(),
                    getDamages(),
                    configuration.cargoType?.id,
                    configuration.operationStep?.id,
                    configuration.terminal.id,
                    operator,
                    cargoCode,
                    findCargoResponse?.mrkNumber,
                    findCargoResponse?.grimaldiContainer,
                    findCargoResponse?.cargoId,
                    getPhotoNames(cargoCode),
                    configuration.voyage?.id?: -1,
                    configuration.shippingLine?.id?: -1,
                    imei,
                    badgeId = loginRepository.getLoginData().badgeId
                )
                val result = repository.uploadData(formSubmissionRequest).await()
                withContext(dispatchers.main) {
                    if (result.isSuccess) {

                    } else {

                    }

                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun getDamages(): List<FormDamage>? {
        val formDamageList = mutableListOf<FormDamage>()
        for (damage in DamagesActivity.currentDamages) {
            formDamageList.add(damage.toFormDamage())
        }
        return formDamageList
    }

    private suspend fun getPhotoNames(cargoCode: String?): List<String> {
        return repository.getImages(cargoCode ?: return emptyList()).values.map {
            it.name ?: ""
        }
    }


    fun findCargoLocal(cargoNumber: String?, imei: String) {
        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {
                //already configured
                val config = _configuration.value
                val findCargoResponse = cargoNumber?.let {
                    repository.findCargo(
                        config?.cargoType?.id?.toString() ?: "",
                        config?.operationStep?.id ?: 0,
                        config?.terminal?.id ?: 0,
                        config?.shippingLine?.value ?: "",
                        config?.voyage?.id ?: -1,
                        imei,
                        it.trim(),
                        config?.voyage?.id ?: 0

                    )?.await()
                }

                Timber.v("fiy: %s", findCargoResponse)
                //addLastSelectedVoyage(findCargoResponse
                val formResponse = addSelectedShippingLine(addSelectedVoyage(findCargoResponse))
                println("Aminu $formResponse")
                withContext(dispatchers.main) {
                    if (findCargoResponse?.isSuccess == true) {
                        //_findCargoResponse.value = formResponse
                        submitForm(findCargoResponse,cargoNumber,imei)
                        deleteChassisNumber(cargoNumber)
                        Log.d("deleteChassisNumber", "findCargoLocal: Deleted")
                        return@withContext

                        Timber.v("findCargoResponse: %s", formResponse)
                    } else {
                        Timber.e("Find Cargo failed with message =%s", formResponse?.message)
                        _findCargoHolder.value = formResponse
                    }
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }
    }

    fun findCargo(cargoNumber: String?, imei: String) {
        //validate
        if (cargoNumber.isNullOrEmpty()) {
            _cargoNumberValidation.value = R.string.cargo_number_invalid_message
            return
        }
        if (_networkState.value?.peekContent() == NetworkState.LOADING) return
        _networkState.value = Event(NetworkState.LOADING)

        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {
                //already configured
                val config = _configuration.value
                val findCargoResponse = repository.findCargo(
                    config?.cargoType?.id?.toString() ?: "",
                    config?.operationStep?.id ?: 0,
                    config?.terminal?.id ?: 0,
                    config?.shippingLine?.value ?: "",
                    config?.voyage?.id ?: -1,
                    imei,
                    cargoNumber.trim(),
                    config?.voyage?.id ?: 0

                )?.await()

                Timber.v("fiy: %s", findCargoResponse)
                //addLastSelectedVoyage(findCargoResponse
                val formResponse = addSelectedShippingLine(addSelectedVoyage(findCargoResponse))
                println("Aminu $formResponse")
                withContext(dispatchers.main) {
                    if (findCargoResponse?.isSuccess == true) {
                        _findCargoResponse.value = formResponse
                        Timber.v("findCargoResponse: %s", formResponse)
                    } else {
                        Timber.e("Find Cargo failed with message =%s", formResponse?.message)
                        _findCargoHolder.value = formResponse
                        val cargoNotFoundResponse = CargoNotFoundResponse(
                            formResponse?.message,
                            Constants.SHIP_SIDE.equals(
                                config?.operationStep?.value,
                                ignoreCase = true
                            )
                        )
                        _errorMessage.value = cargoNotFoundResponse
                    }
                    _networkState.value = Event(NetworkState.LOADED)
                }
            } catch (e: Throwable) {
                Timber.e(e)
                withContext(dispatchers.main) {
                    _networkState.value =
                        Event(
                            NetworkState.error(e)
                        )
                }
            }
        }
    }

    private fun addSelectedShippingLine(findCargoResponse: FindCargoResponse?): FindCargoResponse? {
        val shippingLine = repository.getSelectedConfigAsync().shippingLine
        var newResponse = findCargoResponse
        shippingLine?.let {
            val newValue = findCargoResponse?.values?.toMutableList()
            newValue?.add(FormValue(value = it.value)
                .apply { id = 73 })
            newResponse = findCargoResponse?.copy(values = newValue)
        }
        return newResponse
    }

    private fun addSelectedVoyage(findCargoResponse: FindCargoResponse?): FindCargoResponse? {
        val voyage = repository.getSelectedConfigAsync().voyage
        var newResponse = findCargoResponse
        voyage?.let {
            val newValue = findCargoResponse?.values?.toMutableList()
            newValue?.add(FormValue(value = it.value)
                .apply { id = 58 })
            newResponse = findCargoResponse?.copy(values = newValue)
        }
        return newResponse
    }

    private suspend fun getVoyageId(): Int {
        val form = repository.getFormConfigAsync().await()

        val voyageForm = form.data.filter {
            it.type == FormType.VOYAGE.type
        }
        return if (voyageForm.isNotEmpty()) {
            voyageForm[0].id ?: VOYAGE_ID
        } else {
            VOYAGE_ID
        }
    }

    fun continueToUploadCargo() {
        val findCargoResponse = _findCargoHolder.value
        findCargoResponse?.cargoId = 0
        _findCargoResponse.value = findCargoResponse
    }


    fun openBarcodeScan() {
        _scan.value = Event(Unit)
    }


    fun openDeviceConfiguration() {
        _openDeviceConfiguration.value =
            Event(Unit)
    }

    fun handleNavVoyageClick() {
        _openVoyage.postValue(Event(Unit))
    }

    private fun scheduleCheckLoginWorker() {
        CheckLoginWorker.scheduleWork(context)
    }

    fun openEnterImei() {
        viewModelScope.launch {
            mutableImei.postValue(
                Event(
                    imeiRepository.getIMEI()
                )
            )
        }
    }

    fun updateImei(imei: String) {
        viewModelScope.launch {
            imeiRepository.setIMEI(imei)
        }
    }

    fun updateAppVersion() {
        viewModelScope.launch {
            mutableUpdateAppVersion.postValue(DataState.Loading)
            try {
                val response = loginRepository.updateAppVersion()
                if (response.success == true) {
                    mutableUpdateAppVersion.postValue(DataState.Success(Unit))
                } else {
                    mutableUpdateAppVersion.postValue(DataState.Error(response.message))
                }
            } catch (e: Exception) {
                Timber.e(e, "Error while updating app version")
                mutableUpdateAppVersion.postValue(DataState.Error(e.localizedMessage))
            }
        }
    }

    /* fun mapSelectedVesselToFindCargoResponse(it: FindCargoResponse?): LiveData<FindCargoResponse?> {
         val result = MutableLiveData<FindCargoResponse?>()
         viewModelScope.launch {
             val selectedVoyage = voyageRepository.getLastSelectedVoyage()
            val voyage = it?.values?.firstOrNull { it.id == 58 }
             if(voyage == null){
                 val newValue = it?.values?.toMutableList()
                     newValue?.add(
                     FormValue("Voyage: ${selectedVoyage?.vesselName}").apply {
                         id = 58
                     }
                 )
                 result.value = it?.copy(values = newValue)
             } else {
                 val modifiedValues = it.values.map {
                     if(it.id == 58){
                         it.value = "Voyage: ${selectedVoyage?.vesselName}"
                     }
                     it
                 }
                 result.value = it.copy(values = modifiedValues)
             }


         }
        return result
     }
 */
}