package ptml.releasing.configuration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.repository.ImeiRepository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.livedata.Event
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.configuration.models.*
import timber.log.Timber
import javax.inject.Inject


class ConfigViewModel @Inject constructor(
    private val imeiRepository: ImeiRepository,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    val configResponse = MutableLiveData<AdminConfigResponse>()
    fun getConfigResponse(): LiveData<AdminConfigResponse> = configResponse

    private val operationStepList = MutableLiveData<List<ReleasingOperationStep>>()
    fun getOperationStepList(): LiveData<List<ReleasingOperationStep>> = operationStepList

    private val terminalList = MutableLiveData<List<ReleasingTerminal>>()
    fun getTerminalList(): LiveData<List<ReleasingTerminal>> = terminalList

    val networkState = MutableLiveData<Event<NetworkState>>()
    fun getNetworkState(): LiveData<Event<NetworkState>> = networkState

    val savedSuccess = MutableLiveData<Event<Boolean>>()
    fun getSavedSuccess(): LiveData<Event<Boolean>> = savedSuccess

    val configuration  = _configuration

    fun getConfig(imei: String) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return
        networkState.postValue(
            Event(
                NetworkState.LOADING
            )
        )
        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {
                Timber.d("Getting configuration")
                val response = repository.getAdminConfigurationAsync(imei).await()

                withContext(dispatchers.main) {
                    Timber.d("Configuration gotten: %s", response)
                    configResponse.postValue(response)
                    val config = repository.getSelectedConfigAsync()
                    Timber.d("Configuration gotten: %s", config)
                    _configuration.postValue(config)
                    Timber.e("Loading done: %s", response)
                    networkState.postValue(
                        Event(
                            NetworkState.LOADED
                        )
                    )
                }
            } catch (e: Throwable) {
                Timber.e(e)
                println("In here: ${e.localizedMessage}")
                networkState.postValue(
                    Event(
                        NetworkState.error(e)
                    )
                )
            }
        }


    }

    fun setConfig(
        terminal: ReleasingTerminal?,
        operationStep: ReleasingOperationStep?,
        cargoType: CargoType?,
        checked: Boolean,
        imei: String
    ) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return
        networkState.postValue(Event(NetworkState.LOADING))
//        operationStep.id = 32 //TODO Remove this in production
        val configuration =
            Configuration(terminal ?: return, operationStep ?: return, cargoType ?: return, checked)
        compositeJob = CoroutineScope(dispatchers.db).launch {
            try {
                repository.setSavedConfigAsync(configuration)
                val result = repository.setConfigurationDeviceAsync(
                    cargoTypeId = cargoType.id,
                    terminal = terminal.id,
                    operationStepId = operationStep.id,
                    imei = imei
                ).await()
                Timber.d("Result gotten: %s", result)
                repository.setConfigured(true)
                savedSuccess.postValue(Event(true))
                networkState.postValue( Event(NetworkState.LOADED))
            } catch (e: Throwable) {
                Timber.e(e)
                networkState.postValue(
                    Event(NetworkState.error(e))
                )
            }

        }
    }

    fun cargoTypeSelected(cargoType: CargoType) {
        //populate
        operationStepList.postValue(getOperationStepForCargo(cargoType))
        terminalList.postValue(getTerminalsCargo(cargoType))
    }

    fun refreshConfiguration(imei: String) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return
        networkState.postValue(
            Event(
                NetworkState.LOADING
            )
        )

        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {
                Timber.d("Refreshing configuration")
                val response = repository.downloadAdminConfigurationAsync(imei).await()

                withContext(dispatchers.main) {
                    Timber.d("Configuration gotten: %s", response)
                    configResponse.postValue(response)
                    Timber.d("Checking if there is a saved configuration")
                    Timber.e("Loading done: %s", response)
                    networkState.postValue(
                        Event(
                            NetworkState.LOADED
                        )
                    )
                }
            } catch (e: Throwable) {
                Timber.e(e)
                networkState.postValue(
                    Event(
                        NetworkState.error(e)
                    )
                )
            }
        }


    }


    private fun getOperationStepForCargo(cargoType: CargoType): MutableList<ReleasingOperationStep> {
        val list = mutableListOf<ReleasingOperationStep>()
        for (operationStep in configResponse.value?.operationStepList ?: mutableListOf()) {
            if (operationStep.cargo_type == cargoType.id) {
                Timber.d("Operation step has  a category_id: %s", cargoType.id)
                list.add(operationStep)
            } else {
                Timber.d("Operation step is does not have a category_id: %s", cargoType.id)
            }
        }
        return list
    }


    private fun getTerminalsCargo(cargoType: CargoType): MutableList<ReleasingTerminal> {
        val list = mutableListOf<ReleasingTerminal>()
        for (terminal in configResponse.value?.terminalList ?: mutableListOf()) {
            if (terminal.categoryTypeId == cargoType.id) {
                Timber.d("terminal has  a category_id: %s", cargoType.id)
                list.add(terminal)
            } else {
                Timber.d("terminal  does not have a category_id: %s", cargoType.id)
            }
        }
        return list
    }

    fun updateImei(imei: String) {
        viewModelScope.launch {
            imeiRepository.setIMEI(imei)
        }
    }
}