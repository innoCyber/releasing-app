package ptml.releasing.ui.setup

import androidx.lifecycle.MutableLiveData
import io.reactivex.Scheduler
import ptml.releasing.data.ReleasingRepository
import ptml.releasing.db.models.response.base.BaseResponse
import ptml.releasing.di.modules.rx.OBSERVER_ON
import ptml.releasing.di.modules.rx.SUBSCRIBER_ON
import ptml.releasing.ui.base.BaseViewModel
import ptml.releasing.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class SetupActivityViewModel @Inject constructor(
    var repository: ReleasingRepository,
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