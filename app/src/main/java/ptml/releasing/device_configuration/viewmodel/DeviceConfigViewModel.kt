package ptml.releasing.device_configuration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.remoteconfig.RemoteConfigManger
import ptml.releasing.app.utils.SingleLiveEvent
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import timber.log.Timber
import javax.inject.Inject

class DeviceConfigViewModel @Inject constructor(
    repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {


    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState


    private val _openSearchActivity = SingleLiveEvent<Unit>()
    val openSearchActivity: LiveData<Unit> = _openSearchActivity

    private val _showDeviceError = SingleLiveEvent<Unit>()
    val showDeviceError: LiveData<Unit> = _showDeviceError


    fun
            verifyDeviceId(imei: String) {
        if (networkState.value == NetworkState.LOADING) return
        _networkState.postValue(NetworkState.LOADING)
        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {
                val response = repository.verifyDeviceIdAsync(imei).await()
                withContext(appCoroutineDispatchers.main) {
                    if (response.isSuccess) {
                        _openSearchActivity.postValue(Unit)
                        repository.setImei(imei)
                        repository.setFirst(false)
                    } else {
                        _showDeviceError.postValue(Unit)
                    }
                    _networkState.postValue(NetworkState.LOADED)
                }
            } catch (it: Throwable) {
                Timber.e(it, "Error occurred")
                _networkState.postValue(NetworkState.error(it))
            }
        }

    }


    fun checkIfFirst(): Boolean {
        return repository.isFirstAsync()
    }


}