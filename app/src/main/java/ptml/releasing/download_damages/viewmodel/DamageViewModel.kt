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
import ptml.releasing.download_damages.model.Damage
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class DamageViewModel @Inject constructor(
        repository: Repository,
        appCoroutineDispatchers: AppCoroutineDispatchers) : BaseViewModel(repository, appCoroutineDispatchers) {


    private val responseMutable = MutableLiveData<List<Damage>>()
    private val networkStateMutable = MutableLiveData<NetworkState>()

    //expose as an immutable live data
    val response: LiveData<List<Damage>> = responseMutable
    val networkState: LiveData<NetworkState> = networkStateMutable


    fun getDamages(imei:String) {
        if (networkStateMutable.value == NetworkState.LOADING) return

        networkStateMutable.postValue(NetworkState.LOADING)
        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {


                val response = repository.getDamagesAsync(imei)?.await()
                withContext(appCoroutineDispatchers.main) {
                    if (response?.data?.isNotEmpty() == true) {
                        responseMutable.postValue(response.data)
                        networkStateMutable.postValue(NetworkState.LOADED)
                    } else {
                        networkStateMutable.postValue(NetworkState.error(Exception("Response received was unexpected")))
                    }

                }
            } catch (it: Throwable) {

                Timber.e(it, "Error occurred")
                networkStateMutable.postValue(NetworkState.error(it))
            }
        }


    }


    fun downloadDamagesFromServer(imei:String) {
        if (networkStateMutable.value == NetworkState.LOADING) return

        networkStateMutable.postValue(NetworkState.LOADING)
        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {


                val response = repository.downloadDamagesAsync(imei)?.await()
                withContext(appCoroutineDispatchers.main) {
                    if (response?.data?.isNotEmpty() == true) {
                        responseMutable.postValue(response.data)
                        networkStateMutable.postValue(NetworkState.LOADED)
                    } else {
                        networkStateMutable.postValue(NetworkState.error(Exception("Response received was unexpected")))
                    }

                }
            } catch (it: Throwable) {

                Timber.e(it, "Error occurred")
                networkStateMutable.postValue(NetworkState.error(it))
            }
        }


    }

}