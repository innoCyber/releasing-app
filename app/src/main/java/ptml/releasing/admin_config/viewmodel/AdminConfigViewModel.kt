package ptml.releasing.admin_config.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import javax.inject.Inject

class AdminConfigViewModel @Inject constructor(repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers,
                                               updateChecker: RemoteConfigUpdateChecker
) :
    BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    private val _openConfig = MutableLiveData<Unit>()
    private val _openPrinterSettings = MutableLiveData<Unit>()
    private val _openDownloadDamages = MutableLiveData<Boolean>()
    private val _openQuickRemark = MutableLiveData<Boolean>()
    private val _serverUrl = MutableLiveData<String?>()

    private val _firstTimeLogin = MutableLiveData<Boolean>()
    private val _firstTimeFindCargo = MutableLiveData<Boolean>()
    private var first = true
    private val _openSearch = MutableLiveData<Boolean>()

    val openConfig: LiveData<Unit> = _openConfig
    val openQuickRemark: LiveData<Boolean> = _openQuickRemark
    val openDownloadDamages: LiveData<Boolean> = _openDownloadDamages
    val openPrinterSettings: LiveData<Unit> = _openPrinterSettings
    val openSearch: LiveData<Boolean> = _openSearch
    val serverUrl:LiveData<String?> = _serverUrl


    fun openConfig() {
        _openConfig.postValue(Unit)
    }

    fun openPrinterSetting() {
        _openPrinterSettings.postValue(Unit)
    }

    fun openDownloadDamages() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(appCoroutineDispatchers.main) {
                _openDownloadDamages.postValue(configured)
            }
        }
    }

    fun openQuickRemark() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(appCoroutineDispatchers.main) {
                _openQuickRemark.postValue(configured)
            }
        }
    }

    private fun navigateToLoginIfFirstTime() {
        if (first) {
            _firstTimeLogin.postValue(true)
            first = false
        }
    }

    private fun navigateToFindCargoIfFirstTime() {
        if (first) {
            _firstTimeFindCargo.postValue(true)
            first = false
        }
    }

    fun openSearch() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(appCoroutineDispatchers.main) {
                _openSearch.postValue(configured)
            }
        }
    }

    fun openServer(){
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val serverUrl = repository.getServerUrl()
            withContext(appCoroutineDispatchers.main){
                _serverUrl.postValue(serverUrl)
            }
        }
    }

    fun saveServerUrl(url:String?){
        compositeJob  = CoroutineScope(appCoroutineDispatchers.db).launch {
            repository.saveServerUrl(url)
        }
    }


    override fun handleDeviceConfigured(configured: Boolean) {
        super.handleDeviceConfigured(configured)
        if (configured) {
            navigateToFindCargoIfFirstTime()
        } else {
            navigateToLoginIfFirstTime()
        }
    }
}