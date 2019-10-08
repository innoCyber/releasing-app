package ptml.releasing.cargo_search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.R
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.Event
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.cargo_search.model.CargoNotFoundResponse
import ptml.releasing.cargo_search.model.FindCargoResponse
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    private val _openAdmin = MutableLiveData<Event<Unit>>()
    val openAdMin: LiveData<Event<Unit>> = _openAdmin

    private val _verify = MutableLiveData<Event<Unit>>()
    val verify: LiveData<Event<Unit>> = _verify

    private val _scan = MutableLiveData<Event<Unit>>()
    val scan: LiveData<Event<Unit>> = _scan

    protected val _openDeviceConfiguration = MutableLiveData<Event<Unit>>()
    val openDeviceConfiguration: LiveData<Event<Unit>> = _openDeviceConfiguration

    private val _noOperator = MutableLiveData<Event<Unit>>()
    val noOperator: LiveData<Event<Unit>> = _noOperator

    private val _networkState = MutableLiveData<Event<NetworkState>>()
    val networkState: LiveData<Event<NetworkState>> = _networkState

    private val _cargoNumberValidation = MutableLiveData<Int>()
    val cargoNumberValidation: LiveData<Int> = _cargoNumberValidation

    private val _findCargoResponse = MutableLiveData<FindCargoResponse>()
    val findCargoResponse: LiveData<FindCargoResponse> = _findCargoResponse

    private val _findCargoHolder = MutableLiveData<FindCargoResponse>()

    private val _errorMessage = MutableLiveData<CargoNotFoundResponse>()
    val errorMessage: LiveData<CargoNotFoundResponse> = _errorMessage



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
        _networkState.value = Event(NetworkState.LOADING)

        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {
                //check if there is an operator
                val operator = repository.getOperatorName()
                if (operator == null) {
                    withContext(appCoroutineDispatchers.main) {
                        _noOperator.value = Event(Unit)
                        _networkState.value = Event(NetworkState.LOADED)
                    }
                    return@launch
                }

                //already configured
                val config = _configuration.value
                val findCargoResponse = repository.findCargo(
                    config?.cargoType?.id ?: 0,
                    config?.operationStep?.id ?: 0,
                    config?.terminal?.id ?: 0,
                    imei,
                    cargoNumber.trim()
                )?.await()
                withContext(appCoroutineDispatchers.main) {
                    if (findCargoResponse?.isSuccess == true) {
                        Timber.v("findCargoResponse: %s", findCargoResponse)
                        _findCargoResponse.value = findCargoResponse
                    } else {
                        Timber.e("Find Cargo failed with message =%s", findCargoResponse?.message)

                        _findCargoHolder.value = findCargoResponse
                        val cargoNotFoundResponse = CargoNotFoundResponse(
                            findCargoResponse?.message,
                            Constants.SHIP_SIDE.toLowerCase(Locale.US) == config?.operationStep?.value?.toLowerCase(Locale.US)
                        )
                        _errorMessage.value = cargoNotFoundResponse
                    }
                    _networkState.value = Event(NetworkState.LOADED)
                }
            } catch (e: Throwable) {
                Timber.e(e)
                withContext(appCoroutineDispatchers.main) {
                    _networkState.value = Event(NetworkState.error(e))
                }
            }
        }
    }

    fun continueToUploadCargo() {
        val findCargoResponse = _findCargoResponse.value
        findCargoResponse?.cargoId = 0
        _findCargoResponse.value = findCargoResponse
    }


    fun openBarcodeScan() {
        _scan.value = Event(Unit)
    }


    fun openDeviceConfiguration() {
        _openDeviceConfiguration.value = Event(Unit)
    }


}