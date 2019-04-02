package ptml.releasing.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

class HomeViewModel @Inject constructor( var repository: Repository,  var appCoroutineDispatchers: AppCoroutineDispatchers) : BaseViewModel() {
    private val _openConfiguration = MutableLiveData<Boolean>()
    private val _openDownloadDamages = MutableLiveData<Boolean>()
    private val _openSearch = MutableLiveData<Boolean>()
    private val _isConfigured = MutableLiveData<Boolean>()

    val openConfiguration: LiveData<Boolean> = _openConfiguration
    val openDownloadDamages: LiveData<Boolean> = _openDownloadDamages
    val openSearch: LiveData<Boolean> = _openSearch
    val isConfigured: LiveData<Boolean> = _isConfigured

    fun openConfiguration() {
        _openConfiguration.postValue(true)
    }

    fun openSearch(){
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync().await()
            withContext(appCoroutineDispatchers.main){
                _openSearch.postValue(configured)
            }
        }
    }

    fun openDownloadDamages(){
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync().await()
            withContext(appCoroutineDispatchers.main){
                _openDownloadDamages.postValue(configured)
            }
        }
    }

    fun checkIfConfigured(){
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync().await()
            withContext(appCoroutineDispatchers.main){
                _isConfigured.postValue(configured)
            }
        }
    }
}