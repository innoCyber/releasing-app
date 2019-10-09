package ptml.releasing.cargo_info.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.form.FormSubmission
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Event
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.cargo_info.model.FormDamage
import ptml.releasing.cargo_info.model.FormDataWrapper
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.damages.view.DamagesActivity
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemark
import timber.log.Timber
import javax.inject.Inject

class CargoInfoViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    private val _goBack = MutableLiveData<Event<Boolean>>()
    val goBack: LiveData<Event<Boolean>> = _goBack

    private val _networkState = MutableLiveData<Event<NetworkState>>()
    val networkState: LiveData<Event<NetworkState>> = _networkState

    private val _errorMessage = MutableLiveData<Event<String?>>()
    val errorMessage: LiveData<Event<String?>> = _errorMessage

    private val _submitSuccess = MutableLiveData<Event<Unit>>()
    val submitSuccess: LiveData<Event<Unit>> = _submitSuccess

    private val _noOperator = MutableLiveData<Event<Unit>>()
    val noOperator: LiveData<Event<Unit>> = _noOperator

    private val _formConfig = MutableLiveData<FormDataWrapper>()
    val formConfig: LiveData<FormDataWrapper> = _formConfig

    private val _printerSettings = MutableLiveData<Settings>()
    val printerSettings: LiveData<Settings> = _printerSettings

    fun goBack() {
        _goBack.postValue(Event(true))
    }

    fun getFormConfig(imei: String) {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val map = mutableMapOf<Int, QuickRemark>()
            val formConfig = repository.getFormConfigAsync().await()
            val remarks = repository.getQuickRemarkAsync(imei)?.await()
            for (remark in remarks?.data ?: mutableListOf()) {

                map[remark.id ?: return@launch] = remark
            }
            val wrapper = FormDataWrapper(map, formConfig)
            withContext(appCoroutineDispatchers.main) {
                _formConfig.postValue(wrapper)
            }
        }
    }

    fun getSettings() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val settings = repository.getSettings()
            withContext(appCoroutineDispatchers.main) {
                _printerSettings.postValue(settings)
            }
        }
    }

    fun submitForm(
        formSubmission: FormSubmission,
        cargoCode: String?,
        cargoId: Int?,
        imei: String?
    ) {
        if (_networkState.value?.peekContent() == NetworkState.LOADING) return
        _networkState.postValue(Event(NetworkState.LOADING))
        CoroutineScope(appCoroutineDispatchers.network).launch {
            try {

                val operator = repository.getOperatorName()
                if (operator == null) {
                    withContext(appCoroutineDispatchers.main) {
                        _noOperator.value = Event(Unit)
                        _networkState.value = Event(NetworkState.LOADED)
                    }
                    return@launch
                }

                formSubmission.submit()
                val configuration = repository.getSavedConfigAsync()
                val formSubmissionRequest = FormSubmissionRequest(
                    formSubmission.valuesList,
                    formSubmission.selectionList,
                    getDamages(),
                    configuration.cargoType.id, configuration.operationStep.id,
                    configuration.terminal.id, operator, cargoCode, cargoId, imei
                )
                val result = repository.uploadData(formSubmissionRequest).await()

                withContext(appCoroutineDispatchers.main) {
                    if (result.isSuccess) {
                        _submitSuccess.postValue(Event(Unit))
                    } else {
                        _errorMessage.postValue(Event(result.message))
                    }
                    _networkState.postValue(Event(NetworkState.LOADED))

                }
            } catch (e: Exception) {
                Timber.e(e)
                _networkState.postValue(Event(NetworkState.error(e)))
            }
        }
    }

    private fun getDamages(): List<FormDamage>? {
        val formDamageList = mutableListOf<FormDamage>()
        for (damage in DamagesActivity.currentDamages) {
            formDamageList.add(damage.toFormDamage())
        }
        return formDamageList
    }
}