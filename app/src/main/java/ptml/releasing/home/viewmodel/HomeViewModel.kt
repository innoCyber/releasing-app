package ptml.releasing.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(repository, appCoroutineDispatchers) {
    private val _openConfiguration = MutableLiveData<Boolean>()
    private val _openDownloadDamages = MutableLiveData<Boolean>()
    private val _openSearch = MutableLiveData<Boolean>()
    private val _firstTimeLogin = MutableLiveData<Boolean>()
    private val _firstTimeFindCargo = MutableLiveData<Boolean>()
    private var first = true


    val openConfiguration: LiveData<Boolean> = _openConfiguration
    val openDownloadDamages: LiveData<Boolean> = _openDownloadDamages
    val openSearch: LiveData<Boolean> = _openSearch
    val firstTimeLogin: LiveData<Boolean> = _firstTimeLogin
    val firstTimeFindCargo: LiveData<Boolean> = _firstTimeFindCargo


    private fun navigateToLoginIfFirstTime() {
        if (first) {
            _firstTimeLogin.postValue(true)
            first = false
        }
    }

    private fun navigateToFindCargoIfFirstTime(){
        if (first) {
            _firstTimeFindCargo.postValue(true)
            first = false
        }
    }




    fun openConfiguration() {
        _openConfiguration.postValue(true)
    }

    fun openSearch() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(appCoroutineDispatchers.main) {
                _openSearch.postValue(configured)
            }
        }
    }

    fun openDownloadDamages() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(appCoroutineDispatchers.main) {
                _openDownloadDamages.postValue(configured)
            }
        }
    }

    override fun handleDeviceConfigured(configured: Boolean) {
        super.handleDeviceConfigured(configured)
        if(configured){
            navigateToFindCargoIfFirstTime()
        }else{
            navigateToLoginIfFirstTime()
        }
    }


}