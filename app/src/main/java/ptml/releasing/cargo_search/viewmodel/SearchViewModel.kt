package ptml.releasing.cargo_search.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
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
import ptml.releasing.cargo_search.model.CargoNotFoundResponse
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.model.FormOption
import ptml.releasing.form.FormType
import ptml.releasing.form.utils.Constants.VOYAGE_ID
import ptml.releasing.save_time_worker.CheckLoginWorker
import timber.log.Timber
import java.util.*
import javax.inject.Inject

open class SearchViewModel @Inject constructor(
    private val imeiRepository: ImeiRepository,
    private val context: Context,
    private val formMappers: FormMappers,
    repository: Repository,
    dispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, dispatchers) {

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

    private val _findCargoResponse = MutableLiveData<FindCargoResponse>()
    val findCargoResponse: LiveData<FindCargoResponse> = _findCargoResponse

    private val _findCargoHolder = MutableLiveData<FindCargoResponse>()

    private val _errorMessage = MutableLiveData<CargoNotFoundResponse>()
    val errorMessage: LiveData<CargoNotFoundResponse> = _errorMessage

    private val mutableImei = MutableLiveData<Event<String?>>()
    val imeiNumber = mutableImei.asLiveData()

    private val mutableUpdateAppVersion = MutableLiveData<DataState<Unit>>()
    val  updateAppVersion  = mutableUpdateAppVersion.asLiveData()

    init {
        scheduleCheckLoginWorker()
    }

    fun openAdmin() {
        _openAdmin.value = Event(Unit)

    }

    fun verify() {
        _verify.value = Event(Unit)
    }

    fun findCargo(cargoNumber: String?, imei: String) {
        //validate
        if (cargoNumber.isNullOrEmpty()) {
            _cargoNumberValidation.value = R.string.cargo_number_invalid_message
            return
        }

        if (_networkState.value?.peekContent() == NetworkState.LOADING) return
        _networkState.value =
            Event(NetworkState.LOADING)

        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {

                //already configured
                val config = _configuration.value
                val findCargoResponse = repository.findCargo(
                    config?.cargoType?.id ?: 0,
                    config?.operationStep?.id ?: 0,
                    config?.terminal?.id ?: 0,
                    imei,
                    cargoNumber.trim()
                )?.await()
                val formResponse = addLastSelectedVoyage(findCargoResponse)
                withContext(dispatchers.main) {
                    if (findCargoResponse?.isSuccess == true) {
                        Timber.v("findCargoResponse: %s", formResponse)
                        _findCargoResponse.value = formResponse
                    } else {
                        Timber.e("Find Cargo failed with message =%s", formResponse?.message)
                        _findCargoHolder.value = formResponse
                        val cargoNotFoundResponse = CargoNotFoundResponse(
                            formResponse?.message,
                            Constants.SHIP_SIDE.equals(config?.operationStep?.value, ignoreCase = true)
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

    private suspend fun addLastSelectedVoyage(findCargoResponse: FindCargoResponse?): FindCargoResponse? {
        val lastSelectedVoyage = voyageRepository.getLastSelectedVoyage()

        Timber.d("Last selected voyage: $lastSelectedVoyage")
        return if (lastSelectedVoyage != null) {
            val voyageOption = FormOption(
                listOf(formMappers.voyagesMapper.mapFromModel(lastSelectedVoyage).id)
            )
            voyageOption.id = getVoyageId()
            val options =
                findCargoResponse?.options?.toMutableList() ?: mutableListOf()
            options.add(voyageOption)
            val modifiedWithVoyage = findCargoResponse?.copy(options = options)
            modifiedWithVoyage
        } else {
            findCargoResponse
        }
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

}