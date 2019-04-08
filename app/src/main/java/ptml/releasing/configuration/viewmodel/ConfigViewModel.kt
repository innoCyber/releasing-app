package ptml.releasing.configuration.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import ptml.releasing.configuration.models.*
import ptml.releasing.app.data.Repository
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject


class ConfigViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(repository, appCoroutineDispatchers) {

    val configResponse = MutableLiveData<AdminConfigResponse>()
    val operationStepList = MutableLiveData<List<OperationStep>>()
    val networkState = MutableLiveData<NetworkState>()
    val savedSuccess = MutableLiveData<Boolean>()
    val configuration = MutableLiveData<Configuration>()


    val network = Transformations.map(networkState) {
        it
    }
    val configData = Transformations.map(configResponse) {
        it
    }

    fun getConfig(imei: String) {
        if (networkState.value == NetworkState.LOADING) return
        networkState.postValue(NetworkState.LOADING)
        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {
                Timber.d("Getting configuration")
                val response = repository.getAdminConfigurationAsync(imei).await()

                withContext(appCoroutineDispatchers.main) {
                    Timber.d("Configuration gotten: %s", response)
                    configResponse.postValue(response)
                    Timber.d("Checking if there is a saved configuration")
                    val configured = repository.isConfiguredAsync()
                    if (configured) {
                        Timber.d("Configuration was saved before, getting the configuration")
                        val config = repository.getSavedConfigAsync()
                        Timber.d("Configuration gotten: %s", config)
                        configuration.postValue(config)
                    }
                    Timber.e("Loading done: %s", response)
                    networkState.postValue(NetworkState.LOADED)
                }
            } catch (e: Throwable) {
                Timber.e(e)
                System.out.println("In here: ${e.localizedMessage}")
                networkState.postValue(NetworkState.error(e))
            }
        }


    }

    fun setConfig(
        terminal: Terminal,
        operationStep: OperationStep,
        cargoType: CargoType,
        checked: Boolean,
        imei: String
    ) {
        if (networkState.value == NetworkState.LOADING) return
        networkState.postValue(NetworkState.LOADING)
        val configuration = Configuration(terminal, operationStep, cargoType, checked)
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            try {
                repository.setSavedConfigAsync(configuration)
                val result = repository.setConfigurationDeviceAsync(
                    cargoTypeId = cargoType.id,
                    terminal = terminal.id,
                    operationStepId = operationStep.id,
                    imei = imei
                )
                Timber.d("Result gotten: %s", result)
                repository.setConfigured(true)
                savedSuccess.postValue(true)
                networkState.postValue(NetworkState.LOADED)
            } catch (e: Throwable) {
                Timber.e(e)
                networkState.postValue(NetworkState.error(e))
            }

        }
    }

    fun cargoTypeSelected(cargoType: CargoType) {
        //populate
        operationStepList.postValue(getOperationStepForCargo(cargoType))
    }

    private fun getOperationStepForCargo(cargoType: CargoType): MutableList<OperationStep> {
        val list = mutableListOf<OperationStep>()
        for (operationStep in configResponse.value?.operationStepList ?: mutableListOf()) {
            if (operationStep.categoryTypeId == cargoType.id) {
                Timber.d("Operation step has  a category_id: %s", cargoType.id)
                list.add(operationStep)
            } else {
                Timber.d("Operation step is does not have a category_id: %s", cargoType.id)
            }
        }
        return list
    }


}