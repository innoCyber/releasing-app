package ptml.releasing.voyage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.app.exception.AppException
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.livedata.Event
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


    fun fetchVoyages() {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return

        networkState.postValue(
            Event(
                NetworkState.LOADING
            )
        )
        viewModelScope.launch {
            try {
                withContext(dispatchers.db) {
                    val result = voyageRepository.getRecentVoyages()
                    withContext(dispatchers.main) {
                        if (result.isNotEmpty()) {
                            this@VoyageViewModel.response.postValue(result)
                            networkState.postValue(
                                Event(
                                    NetworkState.LOADED
                                )
                            )
                        } else {
                            networkState.postValue(
                                Event(
                                    NetworkState.error(
                                        AppException(
                                            NO_VOYAGES_MSG
                                        )
                                    )
                                )
                            )
                        }
                    }
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

    fun downloadVoyages() {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return

        networkState.postValue(
            Event(
                NetworkState.LOADING
            )
        )
        viewModelScope.launch {
            try {
                withContext(dispatchers.network) {
                    val result = voyageRepository.downloadRecentVoyages()
                    withContext(dispatchers.main) {
                        if (result.isNotEmpty()) {
                            this@VoyageViewModel.response.postValue(result)
                            networkState.postValue(
                                Event(
                                    NetworkState.LOADED
                                )
                            )
                        } else {
                            networkState.postValue(
                                Event(
                                    NetworkState.error(
                                        AppException(
                                            NO_VOYAGES_MSG
                                        )
                                    )
                                )
                            )
                        }
                    }
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

    companion object {
        private const val NO_VOYAGES_MSG = "No voyages found."
    }
}