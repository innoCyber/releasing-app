package ptml.releasing.app.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.SingleLiveEvent
import ptml.releasing.app.utils.UpdateHelper
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import timber.log.Timber
import javax.inject.Inject

open class BaseViewModel @Inject constructor(
    protected val updateChecker: RemoteConfigUpdateChecker,
    protected val repository: Repository,
    protected val appCoroutineDispatchers: AppCoroutineDispatchers
) : ViewModel() {

    val updateLoadingState = updateChecker.updateCheckState

    private val _updateQuickRemarkLoadingState = MutableLiveData<NetworkState>()
    val updateQuickRemarkLoadingState: LiveData<NetworkState> = _updateQuickRemarkLoadingState

    private val _updateDamagesLoadingState = MutableLiveData<NetworkState>()
    val updateDamagesLoadingState: LiveData<NetworkState> = _updateDamagesLoadingState

    private val _showMustUpdateApp = SingleLiveEvent<Unit>()
    val showMustUpdateApp: LiveData<Unit> = _showMustUpdateApp


    private val _showShouldUpdateApp = SingleLiveEvent<Unit>()
    val showShouldUpdateApp: LiveData<Unit> = _showShouldUpdateApp

    private val _startDamagesUpdate = SingleLiveEvent<Unit>()
    val startDamagesUpdate: LiveData<Unit> = _startDamagesUpdate

    private val _startQuickRemarksUpdate = SingleLiveEvent<Unit>()
    val startQuickRemarksUpdate: LiveData<Unit> = _startQuickRemarksUpdate

    var compositeJob: Job = Job()

    protected val _openBarCodeScanner = MutableLiveData<Unit>()
    protected val _searchScanned = MutableLiveData<String>()

    protected val _isConfigured = MutableLiveData<Boolean>()
    protected val _operatorName = MutableLiveData<String?>()
    val isConfigured: LiveData<Boolean> = _isConfigured
    private val _savedOperatorName = MutableLiveData<String>()
    private val _logOutOperator = MutableLiveData<String>()
    private val _openOperatorDialog = MutableLiveData<Unit>()
    private val _logOutDialog = MutableLiveData<Unit>()
    val openOperatorDialog: LiveData<Unit> = _openOperatorDialog
    val logOutDialog: LiveData<Unit> = _logOutDialog


    private val _openEnterDialog = MutableLiveData<String>()
    val openEnterDialog = _openEnterDialog


    private val _openConfiguration = MutableLiveData<Unit>()
    protected val _configuration = MutableLiveData<Configuration>()


    val openConfiguration: LiveData<Unit> = _openConfiguration

    val openBarCodeScanner: LiveData<Unit> = _openBarCodeScanner
    val searchScanned: LiveData<String> = _searchScanned

    val operatorName: LiveData<String?> = _operatorName
    val savedConfiguration: LiveData<Configuration> = _configuration
    val savedOperatorName: LiveData<String> = _savedOperatorName
    val logOutOperator: LiveData<String> = _logOutOperator


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


    fun saveOperatorName(name: String?) {
        CoroutineScope(appCoroutineDispatchers.db).launch {
            repository.saveOperatorName(name)
            withContext(appCoroutineDispatchers.main) {
                _savedOperatorName.postValue(name)
                _operatorName.postValue(name)
            }
        }
    }


    fun scanForSearch(scanned: String?) {
        _searchScanned.postValue(scanned)
    }


    fun logOutOperator() {
        CoroutineScope(appCoroutineDispatchers.db).launch {
            val oldOperator = repository.getOperatorName()
            repository.saveOperatorName(null)
            withContext(appCoroutineDispatchers.main) {
                _logOutOperator.postValue(oldOperator)
                _operatorName.postValue(null)
            }
        }
    }

    fun getOperatorName() {
        CoroutineScope(appCoroutineDispatchers.db).launch {
            val operator = repository.getOperatorName()
            withContext(appCoroutineDispatchers.main) {
                _operatorName.postValue(operator)
            }
        }
    }

    fun openOperatorDialog() {
        _openOperatorDialog.postValue(Unit)
    }

    fun openEnterDialog() {
        CoroutineScope(appCoroutineDispatchers.db).launch {
            val operator = repository.getOperatorName()
            withContext(appCoroutineDispatchers.main) {
                _openEnterDialog.postValue(operator)
            }
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
            _showMustUpdateApp.value = Unit
            UpdateHelper.showingDialog = true

        } else if (!repository.isFirst() && repository.shouldUpdateApp()) {
            if (!UpdateHelper.noThanksClicked && !UpdateHelper.showingDialog) {
                _showShouldUpdateApp.value = Unit
                UpdateHelper.showingDialog = true
            }
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
        val shouldUpdateApp = updateChecker.shouldUpdateApp()
        repository.setMustUpdateApp(mustUpdateApp)
        repository.setShouldUpdateApp(shouldUpdateApp)
        if (updateChecker.shouldUpdateDamages()) {
            //start intent service to update damages
            _startDamagesUpdate.value = Unit
        }

        if (updateChecker.shouldUpdateQuickRemarks()) {
            //start intent service to update quick remarks
            _startQuickRemarksUpdate.value = Unit
        }
    }

    fun updateQuickRemarks() {
        if (_updateQuickRemarkLoadingState.value == NetworkState.LOADING) {
            Timber.d("Already fetching remote config data...")
            return
        }
        _updateQuickRemarkLoadingState.postValue(NetworkState.LOADING)
        CoroutineScope(appCoroutineDispatchers.network + compositeJob).launch {
            try {
                val imei = repository.getImei()
                repository.downloadQuickRemarkAsync(imei ?: return@launch)?.await()

                val quickRemarkVersion = updateChecker.remoteConfigManger.quickRemarkCurrentVersion
                Timber.d("Downloaded quick remark, updating the local quick remark version to $quickRemarkVersion")
                repository.setQuickCurrentVersion(quickRemarkVersion)
                _updateQuickRemarkLoadingState.postValue(NetworkState.LOADED)
            } catch (t: Throwable) {
                Timber.e(t, "Error occurred while trying to update quick remark")
                _updateQuickRemarkLoadingState.postValue(NetworkState.error(t))
            }
        }
    }

    fun updateDamages() {
        if (_updateDamagesLoadingState.value == NetworkState.LOADING) {
            Timber.d("Already fetching remote config data...")
            return
        }
        _updateDamagesLoadingState.postValue(NetworkState.LOADING)
        CoroutineScope(appCoroutineDispatchers.network + compositeJob).launch {
            try {
                val imei = repository.getImei()
                repository.downloadDamagesAsync(imei ?: return@launch)?.await()

                val damagesVersion = updateChecker.remoteConfigManger.damagesCurrentVersion
                Timber.d("Downloaded damages, updating the local damages version to $damagesVersion")
                repository.setDamagesCurrentVersion(damagesVersion)
                _updateDamagesLoadingState.postValue(NetworkState.LOADED)
            } catch (t: Throwable) {
                Timber.e(t, "Error occurred while trying to update damages")
                _updateDamagesLoadingState.postValue(NetworkState.error(t))
            }
        }
    }

    fun updatingDamages(): Boolean {
        return _updateDamagesLoadingState.value == NetworkState.LOADING
    }

    fun updatingQuickRemarks(): Boolean {
        return _updateQuickRemarkLoadingState.value == NetworkState.LOADING
    }
}