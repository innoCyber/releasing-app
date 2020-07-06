package ptml.releasing.app.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.repository.VoyageRepository
import ptml.releasing.app.data.domain.usecase.GetLoginUseCase
import ptml.releasing.app.data.domain.usecase.LogOutUseCase
import ptml.releasing.app.data.local.LocalDataManager
import ptml.releasing.app.utils.*
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.configuration.models.Configuration
import timber.log.Timber
import java.util.*
import javax.inject.Inject

open class BaseViewModel @Inject constructor(
    protected val updateChecker: RemoteConfigUpdateChecker,
    protected val repository: Repository,
    protected val appCoroutineDispatchers: AppCoroutineDispatchers
) : ViewModel() {

    var imei: String? = null

    @Inject
    lateinit var getLoginUseCase: GetLoginUseCase

    @Inject
    lateinit var logOutUseCase: LogOutUseCase

    @Inject
    lateinit var voyageRepository: VoyageRepository

    @Inject
    lateinit var localDataManager: LocalDataManager

    protected val goToLogin = MutableLiveData<Event<Unit>>()
    fun getGoToLogin(): LiveData<Event<Unit>> = goToLogin

    val updateLoadingState = updateChecker.updateCheckState

    private val _updateQuickRemarkLoadingState = MutableLiveData<NetworkState>()
    val updateQuickRemarkLoadingState: LiveData<NetworkState> = _updateQuickRemarkLoadingState

    private val _updateDamagesLoadingState = MutableLiveData<NetworkState>()
    val updateDamagesLoadingState: LiveData<NetworkState> = _updateDamagesLoadingState

    private val _updateVoyagesLoadingState = MutableLiveData<NetworkState>()
    private val _showUpdateApp = SingleLiveEvent<Unit>()
    val showUpdateApp: LiveData<Unit> = _showUpdateApp


    private val _startDamagesUpdate = SingleLiveEvent<Unit>()
    val startDamagesUpdate: LiveData<Unit> = _startDamagesUpdate

    private val _startQuickRemarksUpdate = SingleLiveEvent<Unit>()
    val startQuickRemarksUpdate: LiveData<Unit> = _startQuickRemarksUpdate

    var compositeJob: Job = Job()

    protected val _openBarCodeScanner = MutableLiveData<Unit>()
    protected val _searchScanned = MutableLiveData<String>()

    protected val _isConfigured = MutableLiveData<Boolean>()
    protected val _operatorName = MutableLiveData<String>()

    val isConfigured: LiveData<Boolean> = _isConfigured

    private val _logOutDialog = MutableLiveData<Unit>()

    val logOutDialog: LiveData<Unit> = _logOutDialog


    private val _openConfiguration = MutableLiveData<Unit>()
    protected val _configuration = MutableLiveData<Configuration>()


    val openConfiguration: LiveData<Unit> = _openConfiguration

    val openBarCodeScanner: LiveData<Unit> = _openBarCodeScanner
    val searchScanned: LiveData<String> = _searchScanned

    val operatorName: LiveData<String> = _operatorName
    val savedConfiguration: LiveData<Configuration> = _configuration


    override fun onCleared() {
        when (compositeJob.isCancelled) {
            false -> {
                compositeJob.cancel()
            }
        }
        super.onCleared()
    }

    fun openConfiguration() {
        _openConfiguration.postValue(Unit)
    }


    fun getSavedConfig() {
        try {
            Timber.d("Checking if there is a saved configuration")

            val configured = repository.isConfiguredAsync()

            _isConfigured.postValue(configured)
            handleDeviceConfigured(configured)
            if (configured) {
                Timber.d("Configuration was saved before, getting the configuration")
                val config = repository.getSavedConfigAsync()
                Timber.d("Configuration gotten: %s", config)
                _configuration.postValue(config)
            }

        } catch (e: Throwable) {
            Timber.e(e)
        }


    }

    fun isConfigured(): Boolean {
        return repository.isConfiguredAsync()
    }

    fun openBarCodeScanner() {
        _openBarCodeScanner.postValue(Unit)
    }


    fun scanForSearch(scanned: String?) {
        _searchScanned.postValue(scanned)
    }


    fun logOutOperator() {
        viewModelScope.launch {
            logOutUseCase.execute()
            goToLogin.postValue(Event(Unit))
        }
    }

    fun getOperatorName() {
        viewModelScope.launch {
            val loginInfo = getLoginUseCase.execute()
            _operatorName.postValue(loginInfo.badgeId)
        }
    }


    fun showLogOutConfirmDialog() {
        _logOutDialog.postValue(Unit)
    }

    open fun handleDeviceConfigured(configured: Boolean) {
        // classes that need to handle this will override
        Timber.d("Configured: %s", configured)
    }

    fun checkForUpdates() {
        if (!repository.isFirst()) {
            updateChecker.check()
        }
    }

    fun checkToShowUpdateAppDialog() {
        if (!repository.isFirst() && repository.mustUpdateApp()) {
            _showUpdateApp.value = Unit
            UpdateHelper.showingDialog = true
        }
    }

    fun checkToResetAppUpdateValues() {
        repository.checkToResetLocalAppUpdateValues()
    }

    fun resetRuntimeShouldUpdate() {
        UpdateHelper.noThanksClicked = true
    }

    fun resetShowingDialog() {
        UpdateHelper.showingDialog = false
    }

    fun applyUpdates() {
        val mustUpdateApp = updateChecker.mustUpdateApp()
        repository.setMustUpdateApp(mustUpdateApp)
        if (updateChecker.shouldUpdateDamages()) {
            _startDamagesUpdate.value = Unit
        }

        if (updateChecker.shouldUpdateQuickRemarks()) {
            _startQuickRemarksUpdate.value = Unit
        }

        if (updateChecker.shouldUpdateVoyages()) {
            updateVoyages()
        }
    }

    fun updateQuickRemarks(imei: String) {
        if (_updateQuickRemarkLoadingState.value == NetworkState.LOADING) {
            Timber.d("Already updating quick remarks...")
            return
        }
        _updateQuickRemarkLoadingState.postValue(NetworkState.LOADING)
        CoroutineScope(appCoroutineDispatchers.network + compositeJob).launch {
            try {
                Timber.d("Updating quick remarks...")
                repository.downloadQuickRemarkAsync(imei)?.await()
                val quickRemarkVersion = updateChecker.remoteConfigManger.quickRemarkVersion
                Timber.d("Downloaded quick remark, updating the local quick remark version to $quickRemarkVersion")
                repository.setQuickCurrentVersion(quickRemarkVersion)
                _updateQuickRemarkLoadingState.postValue(NetworkState.LOADED)
            } catch (t: Throwable) {
                Timber.e(t, "Error occurred while trying to update quick remark")
                _updateQuickRemarkLoadingState.postValue(NetworkState.error(t))
            }
        }
    }

    fun updateDamages(imei: String) {
        if (_updateDamagesLoadingState.value == NetworkState.LOADING) {
            Timber.d("Already updating damages...")
            return
        }
        _updateDamagesLoadingState.postValue(NetworkState.LOADING)
        CoroutineScope(appCoroutineDispatchers.network + compositeJob).launch {
            try {
                Timber.d("Updating damages...")
                repository.downloadDamagesAsync(imei)?.await()

                val damagesVersion = updateChecker.remoteConfigManger.damagesVersion
                Timber.d("Downloaded damages, updating the local damages version to $damagesVersion")
                repository.setDamagesCurrentVersion(damagesVersion)
                _updateDamagesLoadingState.postValue(NetworkState.LOADED)
            } catch (t: Throwable) {
                Timber.e(t, "Error occurred while trying to update damages")
                _updateDamagesLoadingState.postValue(NetworkState.error(t))
            }
        }
    }

    fun updateVoyages() {
        if (updatingVoyages()) {
            Timber.d("Already updating voyages...")
            return
        }
        _updateVoyagesLoadingState.postValue(NetworkState.LOADING)
        viewModelScope.launch {
            try {
                withContext(appCoroutineDispatchers.db) {
                    Timber.d("Updating voyages...")

                    voyageRepository.downloadRecentVoyages()

                    val voyageVersion = updateChecker.remoteConfigManger.voyageVersion
                    Timber.d("Downloaded voyages, updating the local voyages version to $voyageVersion")
                    repository.setVoyagesCurrentVersion(voyageVersion)
                    _updateVoyagesLoadingState.postValue(NetworkState.LOADED)
                }
            } catch (t: Throwable) {
                Timber.e(t, "Error occurred while trying to update voyages")
                _updateVoyagesLoadingState.postValue(NetworkState.error(t))
            }
        }
    }

    fun updatingDamages(): Boolean {
        return _updateDamagesLoadingState.value == NetworkState.LOADING
    }

    private fun updatingVoyages(): Boolean {
        return _updateVoyagesLoadingState.value == NetworkState.LOADING
    }

    fun updatingQuickRemarks(): Boolean {
        return _updateQuickRemarkLoadingState.value == NetworkState.LOADING
    }

    fun onUserInteraction() {
        viewModelScope.launch {
            Timber.d("Updating last user interaction")
            localDataManager.setLastActiveTime(Calendar.getInstance().timeInMillis)
        }
    }
}