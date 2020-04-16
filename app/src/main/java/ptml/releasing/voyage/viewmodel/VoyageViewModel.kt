package ptml.releasing.voyage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Event
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import timber.log.Timber
import javax.inject.Inject

class VoyageViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {


    private val response = MutableLiveData<List<ReleasingVoyage>>()
    fun getResponse(): LiveData<List<ReleasingVoyage>> = response

    private val networkState = MutableLiveData<Event<NetworkState>>()
    fun getNetworkState(): LiveData<Event<NetworkState>> = networkState

    init {
        fetchQuickRemarks()
    }

    private fun fetchQuickRemarks() {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return

        networkState.postValue(Event(NetworkState.LOADING))
        viewModelScope.launch {
            try {
                withContext(appCoroutineDispatchers.db) {
                    val result = voyageRepository.getRecentVoyages()
                    withContext(appCoroutineDispatchers.main) {
                        if (result.isNotEmpty()) {
                            this@VoyageViewModel.response.postValue(result)
                            networkState.postValue(Event(NetworkState.LOADED))
                        } else {
                            networkState.postValue(Event(NetworkState.error(Exception("Response received was unexpected"))))
                        }
                    }
                }
            } catch (it: Throwable) {
                Timber.e(it, "Error occurred")
                networkState.postValue(Event(NetworkState.error(it)))
            }
        }


    }

    fun downloadVoyages() {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return

        networkState.postValue(Event(NetworkState.LOADING))
        viewModelScope.launch {
            try {
                withContext(appCoroutineDispatchers.network) {
                    val result = voyageRepository.downloadRecentVoyages()
                    withContext(appCoroutineDispatchers.main) {
                        if (result.isNotEmpty()) {
                            this@VoyageViewModel.response.postValue(result)
                            networkState.postValue(Event(NetworkState.LOADED))
                        } else {
                            networkState.postValue(Event(NetworkState.error(Exception("Response received was unexpected"))))
                        }
                    }
                }
            } catch (it: Throwable) {
                Timber.e(it, "Error occurred")
                networkState.postValue(Event(NetworkState.error(it)))
            }
        }
    }
}