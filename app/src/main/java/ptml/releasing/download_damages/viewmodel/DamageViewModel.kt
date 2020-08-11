package ptml.releasing.download_damages.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.SingleLiveEvent
import ptml.releasing.app.utils.livedata.Event
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.download_damages.model.Damage
import timber.log.Timber
import javax.inject.Inject

class DamageViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {


    val response = MutableLiveData<List<Damage>>()
    fun getResponse(): LiveData<List<Damage>> = response

    private val networkState = SingleLiveEvent<Event<NetworkState>>()
    fun  getNetworkState(): LiveData<Event<NetworkState>> = networkState


    fun getDamages(imei:String) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return

        networkState.postValue(
            Event(
                NetworkState.LOADING
            )
        )
        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {


                val response = repository.getDamagesAsync(imei)?.await()
                withContext(dispatchers.main) {
                    if (response?.data?.isNotEmpty() == true) {
                        this@DamageViewModel.response.postValue(response.data)
                        networkState.postValue(
                            Event(
                                NetworkState.LOADED
                            )
                        )
                    } else {
                        networkState.postValue(
                            Event(
                                NetworkState.error(Exception("Response received was unexpected"))
                            )
                        )
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


    fun downloadDamagesFromServer(imei:String) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return

        networkState.postValue(
            Event(
                NetworkState.LOADING
            )
        )
        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {


                val response = repository.downloadDamagesAsync(imei)?.await()
                withContext(dispatchers.main) {
                    if (response?.data?.isNotEmpty() == true) {
                        this@DamageViewModel.response.postValue(response.data)
                        networkState.postValue(
                            Event(
                                NetworkState.LOADED
                            )
                        )
                    } else {
                        networkState.postValue(
                            Event(
                                NetworkState.error(Exception("Response received was unexpected"))
                            )
                        )
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

}