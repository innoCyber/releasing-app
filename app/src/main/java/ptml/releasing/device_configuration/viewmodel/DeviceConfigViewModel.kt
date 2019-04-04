package ptml.releasing.device_configuration.viewmodel

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject

class DeviceConfigViewModel @Inject constructor(
        repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(repository, appCoroutineDispatchers) {


    private val imeiLiveData = MutableLiveData<String>()



    val baseLiveData = MutableLiveData<BaseResponse>()


    val networkState = MutableLiveData<NetworkState>()


    fun verifyDeviceId(imei: String) {
        imeiLiveData.value = imei
        if (networkState.value == NetworkState.LOADING) return
        networkState.postValue(NetworkState.LOADING)
        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {

                val response = repository.verifyDeviceIdAsync(imei).await()
                withContext(appCoroutineDispatchers.main) {
                    baseLiveData.postValue(response)
                    networkState.postValue(NetworkState.LOADED)
                }
            } catch (it: Throwable) {

                Timber.e(it, "Error occurred")
                networkState.postValue(NetworkState.error(it))
            }
        }

    }




}