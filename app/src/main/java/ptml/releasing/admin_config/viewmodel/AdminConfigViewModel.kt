package ptml.releasing.admin_config.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.repository.ImeiRepository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.livedata.Event
import ptml.releasing.app.utils.livedata.asLiveData
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import javax.inject.Inject

class AdminConfigViewModel @Inject constructor(
    private val imeiRepository: ImeiRepository,
    repository: Repository, dispatchers: AppCoroutineDispatchers,
    updateChecker: RemoteConfigUpdateChecker
) :
    BaseViewModel(updateChecker, repository, dispatchers) {

    private val _openConfig = MutableLiveData<Event<Unit>>()
    private val _openPrinterSettings = MutableLiveData<Event<Unit>>()
    private val _openDownloadDamages = MutableLiveData<Event<Boolean>>()
    private val _openQuickRemark = MutableLiveData<Event<Boolean>>()
    private val _serverUrl = MutableLiveData<Event<String?>>()
    private val _openSearch = MutableLiveData<Event<Boolean>>()
    private val _openErrorLogs = MutableLiveData<Event<Unit>>()
    private val _openConfirmShowLogs = MutableLiveData<Event<Boolean?>>()


    private val _internetErrorEnabled = MutableLiveData<Boolean>()
    private val _firstTimeLogin = MutableLiveData<Boolean>()
    private val _firstTimeFindCargo = MutableLiveData<Boolean>()
    private var first = true

    val internetErrorEnabled: LiveData<Boolean> = _internetErrorEnabled
    val openConfirmShowLogs: LiveData<Event<Boolean?>> = _openConfirmShowLogs
    val openConfig: LiveData<Event<Unit>> = _openConfig
    val openQuickRemark: LiveData<Event<Boolean>> = _openQuickRemark
    val openDownloadDamages: LiveData<Event<Boolean>> = _openDownloadDamages
    val openPrinterSettings: LiveData<Event<Unit>> = _openPrinterSettings
    val openSearch: LiveData<Event<Boolean>> = _openSearch
    val serverUrl: LiveData<Event<String?>> = _serverUrl
    val openErrorLogs: LiveData<Event<Unit>> = _openErrorLogs

    private val mutableImei = MutableLiveData<Event<String?>>()
    val imeiNumber = mutableImei.asLiveData()


    init {
        fetchInternetErrorState()
    }

    private fun fetchInternetErrorState() {
        compositeJob = CoroutineScope(dispatchers.db).launch {
            val enabled = repository.isInternetErrorLoggingEnabled()
            _internetErrorEnabled.postValue(enabled)
        }
    }

    fun openConfig() {
        _openConfig.postValue(Event(Unit))
    }

    fun openPrinterSetting() {
        _openPrinterSettings.postValue(
            Event(
                Unit
            )
        )
    }

    fun openDownloadDamages() {
        compositeJob = CoroutineScope(dispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(dispatchers.main) {
                _openDownloadDamages.postValue(
                    Event(
                        configured
                    )
                )
            }
        }
    }

    fun openQuickRemark() {
        compositeJob = CoroutineScope(dispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(dispatchers.main) {
                _openQuickRemark.postValue(
                    Event(
                        configured
                    )
                )
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
        compositeJob = CoroutineScope(dispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(dispatchers.main) {
                _openSearch.postValue(
                    Event(
                        configured
                    )
                )
            }
        }
    }

    fun openServerUrl() {
        compositeJob = CoroutineScope(dispatchers.db).launch {
            val serverUrl = repository.getServerUrl()
            withContext(dispatchers.main) {
                _serverUrl.postValue(
                    Event(
                        serverUrl
                    )
                )
            }
        }
    }

    fun openEnterImei() {
        compositeJob = viewModelScope.launch {
            mutableImei.postValue(
                Event(
                    imeiRepository.getIMEI()
                )
            )
        }
    }

    fun saveServerUrl(url: String?) {
        compositeJob = CoroutineScope(dispatchers.db).launch {
            repository.saveServerUrl(url)
            reloadMenu()
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

    fun handleToggleEnableLogs() {
        compositeJob = CoroutineScope(dispatchers.db).launch {
            val enabled = !repository.isInternetErrorLoggingEnabled()
            repository.setInternetErrorLoggingEnabled(enabled)
            _internetErrorEnabled.postValue(enabled)
        }
    }

    fun handleAskToToggleEnableLogs() {
        val enabled = internetErrorEnabled.value
        _openConfirmShowLogs.postValue(
            Event(
                enabled
            )
        )
    }

    fun updateImei(imei: String) {
        viewModelScope.launch {
            imeiRepository.setIMEI(imei)
        }
    }
}