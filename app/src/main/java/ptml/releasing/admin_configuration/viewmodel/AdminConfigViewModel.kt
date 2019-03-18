package ptml.releasing.admin_configuration.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.reactivex.Scheduler
import ptml.releasing.app.data.Repository
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import ptml.releasing.app.di.modules.rx.OBSERVER_ON
import ptml.releasing.app.di.modules.rx.SUBSCRIBER_ON
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class AdminConfigViewModel @Inject constructor(var repository: Repository,
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
        System.out.println("Loading")
        disposable.add(repository.getAdminConfiguration(imei)
                .subscribeOn(subscriberOn)
                .observeOn(observerOn)
                .subscribe({
                    configResponse.postValue(it)
                    System.out.println("In here $it")
                    Timber.e("Loading done: %s", it)
                    networkState.postValue(NetworkState.LOADED)
                }, {
                    Timber.e(it)
                    System.out.println("In here: ${it.localizedMessage}")
                    networkState.postValue(NetworkState.error(it))
                }))
    }
}