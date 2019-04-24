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
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.cargo_search.model.FindCargoResponse
import timber.log.Timber
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(repository, appCoroutineDispatchers) {

    private val _goBack = MutableLiveData<Unit>()
    private val _verify = MutableLiveData<Unit>()
    private val _scan = MutableLiveData<Unit>()
    private val _noOperator = MutableLiveData<Unit>()
    private val _networkState = MutableLiveData<NetworkState>()
    private val _cargoNumberValidation = MutableLiveData<Int>()
    private val _findCargoResponse = MutableLiveData<FindCargoResponse>()
    private val _findCargoHolder = MutableLiveData<FindCargoResponse>()
    private val _errorMessage = MutableLiveData<String>()
    private val _openConfiguration = MutableLiveData<Unit>()

    val networkState: LiveData<NetworkState> = _networkState
    val goBack: LiveData<Unit> = _goBack
    val scan: LiveData<Unit> = _scan
    val noOperator: LiveData<Unit> = _noOperator
    val verify: LiveData<Unit> = _verify
    val cargoNumberValidation: LiveData<Int> = _cargoNumberValidation
    val findCargoResponse: LiveData<FindCargoResponse> = _findCargoResponse
    val errorMessage: LiveData<String> = _errorMessage
    val openConfiguration: LiveData<Unit> = _openConfiguration

    fun goBack() {
        _goBack.postValue(Unit)
    }

    fun verify() {
        _verify.postValue(Unit)
    }

    fun findCargo(cargoNumber: String?, imei: String) {
        //validate
        if (cargoNumber.isNullOrEmpty()) {
            _cargoNumberValidation.postValue(R.string.cargo_number_invalid_message)
            return
        }

        if (_networkState.value == NetworkState.LOADING) return
        _networkState.postValue(NetworkState.LOADING)

        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {
                //check if there is an operator
                val operator = repository.getOperatorName()
                if(operator == null){
                    _noOperator.postValue(Unit)
                    _networkState.postValue(NetworkState.LOADED)
                    return@launch
                }

                //already configured
                val config = _configuration.value
                val findCargoResponse = repository.findCargo(
                    config?.cargoType?.id ?: 0,
                    config?.operationStep?.id ?: 0,
                    config?.terminal?.id ?: 0,
                    imei,
                    cargoNumber
                ).await()
                withContext(appCoroutineDispatchers.main) {
                    findCargoResponse.isSuccess = false
                    if (findCargoResponse.isSuccess) {
                        Timber.v("findCargoResponse: %s", findCargoResponse)
                        _findCargoResponse.postValue(findCargoResponse)
                    } else {
                        findCargoResponse.message = "The cargo number is not recognized"
                        Timber.e("Find Cargo failed with message =%s", findCargoResponse.message)
                        _findCargoHolder.value = findCargoResponse
                        _errorMessage.postValue(findCargoResponse.message)
                    }
                    _networkState.postValue(NetworkState.LOADED)
                }
            } catch (e: Throwable) {
                Timber.e(e)
                _networkState.postValue(NetworkState.error(e))
            }
        }
    }

    fun continueToUploadCargo(){
        _findCargoResponse.postValue(_findCargoHolder.value)
    }


    fun openBarcodeScan() {
        _scan.postValue(Unit)
    }

    fun openConfiguration() {
        _openConfiguration.postValue(Unit)
    }


}