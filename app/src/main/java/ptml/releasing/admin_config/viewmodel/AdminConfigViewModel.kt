package ptml.releasing.admin_config.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Event
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import javax.inject.Inject

class AdminConfigViewModel @Inject constructor(repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers,
                                               updateChecker: RemoteConfigUpdateChecker
) :
    BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    private val _openConfig = MutableLiveData<Event<Unit>>()
    private val _openPrinterSettings = MutableLiveData<Event<Unit>>()
    private val _openDownloadDamages = MutableLiveData<Event<Boolean>>()
    private val _openQuickRemark = MutableLiveData<Event<Boolean>>()
    private val _serverUrl = MutableLiveData<Event<String?>>()
    private val _openSearch = MutableLiveData<Event<Boolean>>()
    private val _openErrorLogs = MutableLiveData<Event<Unit>>()

    private val _firstTimeLogin = MutableLiveData<Boolean>()
    private val _firstTimeFindCargo = MutableLiveData<Boolean>()
    private var first = true

    val openConfig: LiveData<Event<Unit>> = _openConfig
    val openQuickRemark: LiveData<Event<Boolean>> = _openQuickRemark
    val openDownloadDamages: LiveData<Event<Boolean>> = _openDownloadDamages
    val openPrinterSettings: LiveData<Event<Unit>> = _openPrinterSettings
    val openSearch: LiveData<Event<Boolean>> = _openSearch
    val serverUrl:LiveData<Event<String?>> = _serverUrl
    val openErrorLogs: LiveData<Event<Unit>> = _openErrorLogs


    fun openConfig() {
        _openConfig.postValue(Event(Unit))
    }

    fun openPrinterSetting() {
        _openPrinterSettings.postValue(Event(Unit))
    }

    fun openDownloadDamages() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(appCoroutineDispatchers.main) {
                _openDownloadDamages.postValue(Event(configured))
            }
        }
    }

    fun openQuickRemark() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(appCoroutineDispatchers.main) {
                _openQuickRemark.postValue(Event(configured))
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
                _openSearch.postValue(Event(configured))
            }
        }
    }

    fun openServer(){
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val serverUrl = repository.getServerUrl()
            withContext(appCoroutineDispatchers.main){
                _serverUrl.postValue(Event(serverUrl))
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

    fun openErrorLogs() {
        _openErrorLogs.postValue(Event(Unit))
    }
}