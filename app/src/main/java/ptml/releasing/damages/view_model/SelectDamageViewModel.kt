package ptml.releasing.damages.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.damages.view.DamagesActivity
import ptml.releasing.download_damages.model.Damage
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SelectDamageViewModel @Inject constructor(repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers,
                                                updateChecker: RemoteConfigUpdateChecker
) :
    BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    private val _damagesListFiltered = MutableLiveData<List<Damage>>()
    private val _damagesList = MutableLiveData<List<Damage>>()
    private val _networkState = MutableLiveData<NetworkState>()


    val damagesList: LiveData<List<Damage>> = _damagesListFiltered
    val networkState: LiveData<NetworkState> = _networkState

    fun getDamages(imei: String, typeContainer:Int?) {

        if (_networkState.value == NetworkState.LOADING) return
        _networkState.postValue(NetworkState.LOADING)

        try {

            val position = if (DamagesActivity.currentDamagePoint == "BBO"
                || DamagesActivity.currentDamagePoint == "CBO"
                || DamagesActivity.currentDamagePoint == "FBO"
                || DamagesActivity.currentDamagePoint == "BOD"
            ) {
                "C"
            } else {
                "S"
            }


            compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
                val list = repository.getDamagesByPosition(imei, position, typeContainer)
                withContext(appCoroutineDispatchers.main) {
                    _damagesListFiltered.postValue(list)
                    _damagesList.value = list
                    _networkState.postValue(NetworkState.LOADED)
                }
            }
        } catch (e: Throwable) {
            Timber.e(e)
            _networkState.postValue(NetworkState.error(e))
        }
    }

    fun filter(query: String) {

        if (query.isEmpty()) {
            _damagesListFiltered.postValue(_damagesList.value)
            return
        }

        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            delay(500)
            val list = mutableListOf<Damage>()
            for (i in _damagesList.value ?: mutableListOf()) {
                if (i.description.contains(query, true)) {
                    list.add(i)

                }
            }

            withContext(appCoroutineDispatchers.main) {
                Timber.d("List: %s", list)
                _damagesListFiltered.postValue(list)
            }

        }
    }
}