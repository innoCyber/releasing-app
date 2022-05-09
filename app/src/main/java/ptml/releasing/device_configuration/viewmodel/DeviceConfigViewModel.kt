package ptml.releasing.device_configuration.viewmodel

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
import ptml.releasing.app.utils.SingleLiveEvent
import ptml.releasing.app.utils.livedata.Event
import ptml.releasing.app.utils.livedata.asLiveData
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import timber.log.Timber
import javax.inject.Inject

class DeviceConfigViewModel @Inject constructor(
    private val imeiRepository: ImeiRepository,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers,
    updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {


    val networkState = MutableLiveData<Event<NetworkState>>()
    fun getNetworkState(): LiveData<Event<NetworkState>> = networkState

    val openSearchActivity = MutableLiveData<Event<Unit>>()
    fun openSearchActivity(): LiveData<Event<Unit>> = openSearchActivity

    val showDeviceError = SingleLiveEvent<Event<Unit>>()
    fun showDeviceError(): LiveData<Event<Unit>> = showDeviceError

    private val mutableImei = MutableLiveData<Event<String?>>()
    val imeiNumber = mutableImei.asLiveData()

    fun verifyDeviceId(imei: String) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return
        networkState.postValue(
            Event(
                NetworkState.LOADING
            )
        )
        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {
                val response = repository.verifyDeviceIdAsync(imei).await()

                withContext(dispatchers.main) {
                    if (response.isSuccess) {
                        openSearchActivity.postValue(
                            Event(
                                Unit
                            )
                        )
                        repository.setFirst(false)
                    } else {
                        showDeviceError.postValue(
                            Event(
                                Unit
                            )
                        )
                    }
                    networkState.postValue(
                        Event(
                            NetworkState.LOADED
                        )
                    )
                }
            } catch (it: Throwable) {
                Timber.e(it, "Error occurred")
                networkState.postValue(
                    Event(
                        NetworkState.error(it)
                    )
                )
            }
        }
    }

    fun openEnterImei() {
        viewModelScope.launch {
            mutableImei.postValue(
                Event(
                    imeiRepository.getIMEI()
                )
            )
        }
    }

    fun updateImei(imei: String) {
        viewModelScope.launch {
            imeiRepository.setIMEI(imei)
        }
    }

    fun checkIfFirst(): Boolean {
        return repository.isFirst()
    }
}