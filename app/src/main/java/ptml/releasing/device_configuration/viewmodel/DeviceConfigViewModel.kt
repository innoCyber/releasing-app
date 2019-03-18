package ptml.releasing.device_configuration.viewmodel

import androidx.lifecycle.MutableLiveData
import io.reactivex.Scheduler
import ptml.releasing.app.data.Repository
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.di.modules.rx.OBSERVER_ON
import ptml.releasing.app.di.modules.rx.SUBSCRIBER_ON
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class DeviceConfigViewModel @Inject constructor(
    var repository: Repository,
    @param:Named(SUBSCRIBER_ON) var subscriberOn: Scheduler,
    @param:Named(OBSERVER_ON) var observerOn: Scheduler
) : BaseViewModel() {



    private val imeiLiveData = MutableLiveData<String>()

    val baseLiveData = MutableLiveData<BaseResponse>()

    val networkState = MutableLiveData<NetworkState>()


    fun verifyDeviceId(imei: String) {
        imeiLiveData.value = imei

        disposable.add(repository.verifyDeviceId(imei)
            .subscribeOn(subscriberOn)
            .observeOn(observerOn)
            .doOnSubscribe { networkState.postValue(NetworkState.LOADING) }
            .subscribe({
                baseLiveData.postValue(it)
                networkState.postValue(NetworkState.LOADED)
            }, {
                Timber.e(it, "Error occurred")
                networkState.postValue(NetworkState.error(it))
            })
        )
    }

}