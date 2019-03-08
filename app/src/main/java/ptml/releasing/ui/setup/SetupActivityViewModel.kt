package ptml.releasing.ui.setup

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import ptml.releasing.app.App
import ptml.releasing.data.ReleasingRepository
import ptml.releasing.db.models.response.base.BaseResponse
import ptml.releasing.di.modules.rx.OBSERVER_ON
import ptml.releasing.di.modules.rx.SUBSCRIBER_ON
import ptml.releasing.utils.ErrorHandler
import ptml.releasing.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class SetupActivityViewModel @Inject constructor(var repository: ReleasingRepository,
                                                 @param:Named(SUBSCRIBER_ON) var subscriberOn: Scheduler,
                                                 @param:Named(OBSERVER_ON) var observerOn: Scheduler
): ViewModel() {

   private val disposable = CompositeDisposable()

   private val imeiLiveData = MutableLiveData<String>()

    val dataLiveData = Transformations.map(imeiLiveData){
        baseLiveData.value
    }

    val networkLiveData = Transformations.map(imeiLiveData){
        networkState.value
    }



    val baseLiveData = MutableLiveData<BaseResponse>()

    val networkState = MutableLiveData<NetworkState>()


    fun verifyDeviceId(imei:String){
        imeiLiveData.value = imei

        disposable.add(repository.verifyDeviceId(imei)
            .subscribeOn(subscriberOn)
            .observeOn(observerOn)
            .doOnSubscribe {
                System.out.println("doOnSubscribe")
                networkState.postValue(NetworkState.LOADING)  }
            .doOnDispose {  System.out.println("doOnDispose") }
            .doOnError{ System.out.println("doOnError") }
            .doOnComplete{System.out.println("doOnComplete") }
            .subscribe({
            baseLiveData.postValue(it)
                System.out.println("Loaded")
                Timber.e("Loaded")
            networkState.postValue(NetworkState.LOADED)
        }, {
            Timber.e(it, "Error occurred")
                System.out.println("Error")
//            val error = ErrorHandler(context).getErrorMessage(it)
            networkState.postValue(NetworkState.error(it.message))
        }))
    }

    override fun onCleared() {
        if(!disposable.isDisposed){
            disposable.dispose()
        }
        super.onCleared()
    }

}