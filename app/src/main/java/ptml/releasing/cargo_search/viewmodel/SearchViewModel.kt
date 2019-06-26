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
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.SingleLiveEvent
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.cargo_search.model.CargoNotFoundResponse
import ptml.releasing.cargo_search.model.FindCargoResponse
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    private val _openAdmin = SingleLiveEvent<Unit>()
    private val _verify = SingleLiveEvent<Unit>()
    private val _scan = SingleLiveEvent<Unit>()
    private val _networkState = MutableLiveData<NetworkState>()
    private val _cargoNumberValidation = MutableLiveData<Int>()
    private val _findCargoResponse = MutableLiveData<FindCargoResponse>()
    private val _findCargoHolder = MutableLiveData<FindCargoResponse>()
    private val _errorMessage = MutableLiveData<CargoNotFoundResponse>()
    protected val _openDeviceConfiguration = SingleLiveEvent<Unit>()
    val networkState: LiveData<NetworkState> = _networkState

    val openAdMin: LiveData<Unit> = _openAdmin
    val scan: LiveData<Unit> = _scan
    private val _noOperator = SingleLiveEvent<Unit>()
    val noOperator: LiveData<Unit> = _noOperator
    val verify: LiveData<Unit> = _verify
    val cargoNumberValidation: LiveData<Int> = _cargoNumberValidation
    val findCargoResponse: LiveData<FindCargoResponse> = _findCargoResponse
    val errorMessage: LiveData<CargoNotFoundResponse> = _errorMessage
    val openDeviceConfiguration: LiveData<Unit> = _openDeviceConfiguration


    fun openAdmin() {
        _openAdmin.value = Unit

    }

    fun verify() {
        _verify.value = Unit
    }

    fun findCargo(cargoNumber: String?, imei: String) {
        //validate
        if (cargoNumber.isNullOrEmpty()) {
            _cargoNumberValidation.value = R.string.cargo_number_invalid_message
            return
        }

        if (_networkState.value == NetworkState.LOADING) return
        _networkState.value = NetworkState.LOADING

        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {
                //check if there is an operator
                val operator = repository.getOperatorName()
                if (operator == null) {
                    withContext(appCoroutineDispatchers.main) {
                        _noOperator.value = Unit
                        _networkState.value = NetworkState.LOADED
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
                            Constants.SHIP_SIDE.toLowerCase() == config?.operationStep?.value?.toLowerCase()
                        )
                        _errorMessage.value = cargoNotFoundResponse
                    }
                    _networkState.value = NetworkState.LOADED
                }
            } catch (e: Throwable) {
                Timber.e(e)
                withContext(appCoroutineDispatchers.main) {
                    _networkState.value = NetworkState.error(e)
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
        _scan.value = Unit
    }


    fun openDeviceConfiguration() {
        _openDeviceConfiguration.value = Unit
    }


}