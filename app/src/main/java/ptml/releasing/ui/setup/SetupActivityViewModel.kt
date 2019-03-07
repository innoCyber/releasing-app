package ptml.releasing.ui.setup

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.reactivex.disposables.CompositeDisposable
import ptml.releasing.app.App
import ptml.releasing.data.ReleasingRepository
import ptml.releasing.db.models.response.base.BaseResponse
import ptml.releasing.utils.ErrorHandler
import ptml.releasing.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject

class SetupActivityViewModel @Inject constructor(app:App,var repository: ReleasingRepository): AndroidViewModel(app) {

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
        if (networkState.value == NetworkState.LOADING) return
        networkState.postValue(NetworkState.LOADING)
        disposable.add(repository.verifyDeviceId(imei).subscribe({
            baseLiveData.postValue(it)
            networkState.postValue(NetworkState.LOADED)
        }, {
            Timber.e(it, "Error occurred")
            val error = ErrorHandler(getApplication()).getErrorMessage(it)
            networkState.postValue(NetworkState.error(error))
        }))
    }

    override fun onCleared() {
        if(!disposable.isDisposed){
            disposable.dispose()
        }
        super.onCleared()
    }

}