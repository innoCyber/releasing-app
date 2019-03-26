package ptml.releasing.damages.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.damages.model.Damage
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class DamageViewModel @Inject constructor(
        private var repository: Repository,
        private var appCoroutineDispatchers: AppCoroutineDispatchers) : BaseViewModel() {


    private val responseMutable = MutableLiveData<List<Damage>>()
    private val networkStateMutable = MutableLiveData<NetworkState>()

    //expose as an immutable live data
    val response: LiveData<List<Damage>> = responseMutable
    val networkState: LiveData<NetworkState> = networkStateMutable


    fun downloadDamages(imei:String) {
        if (networkStateMutable.value == NetworkState.LOADING) return

        networkStateMutable.postValue(NetworkState.LOADING)
        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {

                val response = repository.downloadDamagesAsync(imei).await()
                //TODO insert to Database
                withContext(appCoroutineDispatchers.main) {
                    if (response.data.isNotEmpty()) {
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