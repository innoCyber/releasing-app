package ptml.releasing.device_configuration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Event
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.SingleLiveEvent
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import timber.log.Timber
import javax.inject.Inject

class DeviceConfigViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers,
    updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {


    val networkState = MutableLiveData<Event<NetworkState>>()
    fun  getNetworkState(): LiveData<Event<NetworkState>> = networkState

    val openSearchActivity = MutableLiveData<Event<Unit>>()
    fun openSearchActivity(): LiveData<Event<Unit>> = openSearchActivity

    val showDeviceError = SingleLiveEvent<Event<Unit>>()
    fun showDeviceError(): LiveData<Event<Unit>> = showDeviceError


    fun verifyDeviceId(imei: String) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return
        networkState.postValue(Event(NetworkState.LOADING))
        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {
                val response = repository.verifyDeviceIdAsync(imei).await()
                withContext(appCoroutineDispatchers.main) {
                    if (response.isSuccess) {
                        openSearchActivity.postValue(Event(Unit))
                        repository.setImei(imei)
                        repository.setFirst(false)
                    } else {
                        showDeviceError.postValue(Event(Unit))
                    }
                    networkState.postValue(Event(NetworkState.LOADED))
                }
            } catch (it: Throwable) {
                Timber.e(it, "Error occurred")
                networkState.postValue(Event(NetworkState.error(it)))
            }
        }

    }


    fun checkIfFirst(): Boolean {
        return repository.isFirst()
    }


}