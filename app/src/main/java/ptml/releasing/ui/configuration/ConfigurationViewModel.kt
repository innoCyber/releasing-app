package ptml.releasing.ui.configuration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.reactivex.Scheduler
import ptml.releasing.data.Repository
import ptml.releasing.db.models.config.response.ConfigurationResponse
import ptml.releasing.di.modules.rx.OBSERVER_ON
import ptml.releasing.di.modules.rx.SUBSCRIBER_ON
import ptml.releasing.ui.base.BaseViewModel
import ptml.releasing.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ConfigurationViewModel @Inject constructor(var repository: Repository,
                                                 @param:Named(SUBSCRIBER_ON) var subscriberOn: Scheduler,
                                                 @param:Named(OBSERVER_ON) var observerOn: Scheduler) : BaseViewModel() {

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
        disposable.add(repository.getAdminConfiguration(imei)
                .subscribeOn(subscriberOn)
                .observeOn(observerOn)
                .subscribe({
                    configResponse.postValue(it)
                    Timber.e("Loading done: %s", it)
                    networkState.postValue(NetworkState.LOADED)
                }, {
                    Timber.e(it)
                    networkState.postValue(NetworkState.error(it))
                }))
    }
}