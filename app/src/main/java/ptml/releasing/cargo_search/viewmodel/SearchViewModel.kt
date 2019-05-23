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
import java.lang.Exception
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(repository, appCoroutineDispatchers) {

    private val _openAdmin = MutableLiveData<Unit>()
    private val _verify = MutableLiveData<Unit>()
    private val _scan = MutableLiveData<Unit>()
    private val _noOperator = MutableLiveData<Unit>()
    private val _networkState = MutableLiveData<NetworkState>()
    private val _cargoNumberValidation = MutableLiveData<Int>()
    private val _findCargoResponse = MutableLiveData<FindCargoResponse>()
    private val _findCargoHolder = MutableLiveData<FindCargoResponse>()
    private val _errorMessage = MutableLiveData<String>()


    val networkState: LiveData<NetworkState> = _networkState
    val openAdMin: LiveData<Unit> = _openAdmin
    val scan: LiveData<Unit> = _scan
    val noOperator: LiveData<Unit> = _noOperator
    val verify: LiveData<Unit> = _verify
    val cargoNumberValidation: LiveData<Int> = _cargoNumberValidation
    val findCargoResponse: LiveData<FindCargoResponse> = _findCargoResponse
    val errorMessage: LiveData<String> = _errorMessage


    fun openAdmin() {
        _openAdmin.postValue(Unit)
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
                    cargoNumber.trim()
                )?.await()
                withContext(appCoroutineDispatchers.main) {
                    if (findCargoResponse?.isSuccess ==  true) {
                        Timber.v("findCargoResponse: %s", findCargoResponse)
                        _findCargoResponse.postValue(findCargoResponse)
                    } else {
                        Timber.e("Find Cargo failed with message =%s", findCargoResponse?.message)
                        if(findCargoResponse?.message?.isEmpty() == false){
                            _findCargoHolder.value = findCargoResponse
                            _errorMessage.postValue(findCargoResponse.message)
                        }else{
                            val e = Exception("Response is null")
                            _networkState.postValue(NetworkState.error(e))
                        }
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
        val findCargoResponse  = _findCargoResponse.value
        findCargoResponse?.cargoId = 0
        _findCargoResponse.postValue(findCargoResponse)
    }


    fun openBarcodeScan() {
        _scan.postValue(Unit)
    }



}