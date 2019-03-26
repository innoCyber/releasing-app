package ptml.releasing.admin_configuration.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import ptml.releasing.app.data.Repository
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject


class AdminConfigViewModel @Inject constructor(
    var repository: Repository,
    var appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel() {

    val configResponse = MutableLiveData<ConfigurationResponse>()
    val networkState = MutableLiveData<NetworkState>()


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
                val response = repository.getAdminConfigurationAsync(imei).await()

                withContext(appCoroutineDispatchers.main) {
                    configResponse.postValue(response)
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
}