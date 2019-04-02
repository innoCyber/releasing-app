package ptml.releasing.admin_configuration.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import ptml.releasing.admin_configuration.models.*
import ptml.releasing.app.data.Repository
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject


class AdminConfigViewModel @Inject constructor(
    var repository: Repository,
    var appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel() {

    val configResponse = MutableLiveData<AdminConfigResponse>()
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
                    val configured = repository.isConfiguredAsync().await()
                    if(configured){
                        Timber.d("Configuration was saved before, getting the configuration")
                        val config = repository.getSavedConfigAsync().await()
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

    fun setConfig(terminal: Terminal, operationStep: OperationStep, cargoType: CargoType, checked: Boolean) {
        if (networkState.value == NetworkState.LOADING) return
        networkState.postValue(NetworkState.LOADING)
        val configuration = Configuration(terminal, operationStep, cargoType, checked)
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            try {
                repository.setSavedConfigAsync(configuration)
                repository.setConfigured(true)
                savedSuccess.postValue(true)
                networkState.postValue(NetworkState.LOADED)
            }catch (e: Throwable) {
                Timber.e(e)
                networkState.postValue(NetworkState.error(e))
            }

        }
    }


}