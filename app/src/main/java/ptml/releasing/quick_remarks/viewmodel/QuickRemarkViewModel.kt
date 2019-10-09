package ptml.releasing.quick_remarks.viewmodel

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
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.quick_remarks.model.QuickRemark
import timber.log.Timber
import javax.inject.Inject

class QuickRemarkViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {


    private val response = MutableLiveData<List<QuickRemark>>()
    fun  getResponse(): LiveData<List<QuickRemark>> = response

    private val networkState = MutableLiveData<Event<NetworkState>>()
    fun getNetworkState(): LiveData<Event<NetworkState>> = networkState


    fun getQuickRemarks(imei: String) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return

        networkState.postValue(Event(NetworkState.LOADING))
        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {

                val response = repository.getQuickRemarkAsync(imei)?.await()
                withContext(appCoroutineDispatchers.main) {
                    if (response?.data?.isNotEmpty() == true) {
                        this@QuickRemarkViewModel.response.postValue(response.data)
                        networkState.postValue(Event(NetworkState.LOADED))
                    } else {
                        networkState.postValue(Event(NetworkState.error(Exception("Response received was unexpected"))))
                    }

                }
            } catch (it: Throwable) {

                Timber.e(it, "Error occurred")
                networkState.postValue(Event(NetworkState.error(it)))
            }
        }


    }


    fun downloadQuickRemarksFromServer(imei: String) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return

        networkState.postValue(Event(NetworkState.LOADING))
        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {


                val response = repository.downloadQuickRemarkAsync(imei)?.await()
                withContext(appCoroutineDispatchers.main) {
                    if (response?.data?.isNotEmpty() == true) {
                        this@QuickRemarkViewModel.response.postValue(response.data)
                        networkState.postValue(Event(NetworkState.LOADED))
                    } else {
                        networkState.postValue(Event(NetworkState.error(Exception("Response received was unexpected"))))
                    }

                }
            } catch (it: Throwable) {

                Timber.e(it, "Error occurred")
                networkState.postValue(Event(NetworkState.error(it)))
            }
        }


    }

}