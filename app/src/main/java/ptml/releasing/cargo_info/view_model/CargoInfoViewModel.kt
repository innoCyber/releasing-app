package ptml.releasing.cargo_info.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.R
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.form.FormSubmission
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.SingleLiveEvent
import ptml.releasing.cargo_info.model.FormDamage
import ptml.releasing.cargo_info.model.FormDataWrapper
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.damages.view.DamagesActivity
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemark
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class CargoInfoViewModel @Inject constructor(repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers) :
    BaseViewModel(repository, appCoroutineDispatchers) {

    private val _goBack = MutableLiveData<Boolean>()
    private val _formConfig = MutableLiveData<FormDataWrapper>()


    val goBack: LiveData<Boolean> = _goBack
    val formConfig: LiveData<FormDataWrapper> = _formConfig


    private val _noOperator = SingleLiveEvent<Unit>()
    val noOperator: LiveData<Unit> = _noOperator

    private val _printerSettings = MutableLiveData<Settings>()
    val printerSettings: LiveData<Settings> = _printerSettings
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState


    private val _errorMessage = MutableLiveData<String>()
    private val _submitSuccess = MutableLiveData<Unit>()

    val errorMessage : LiveData<String> = _errorMessage
    val submitSuccess :LiveData<Unit> = _submitSuccess

    fun goBack() {
        _goBack.postValue(true)
    }

    fun getFormConfig(imei:String) {
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

    fun submitForm(formSubmission: FormSubmission, cargoCode: String?, cargoId:Int?, imei:String?) {
        if (_networkState.value == NetworkState.LOADING) return
        _networkState.postValue(NetworkState.LOADING)
        CoroutineScope(appCoroutineDispatchers.network).launch {
            try {

                val operator = repository.getOperatorName()
                if (operator == null) {
                    withContext(appCoroutineDispatchers.main) {
                        _noOperator.value = Unit
                        _networkState.value = NetworkState.LOADED
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
                    configuration.terminal.id, operator,  cargoCode, cargoId, imei)
                val result = repository.uploadData(formSubmissionRequest).await()

                withContext(appCoroutineDispatchers.main) {
                    if(result.isSuccess){
                        _submitSuccess.postValue(Unit)
                    }else{
                        _errorMessage.postValue(result.message)
                    }
                    _networkState.postValue(NetworkState.LOADED)

                }
            } catch (e: Exception) {
                Timber.e(e)
                _networkState.postValue(NetworkState.error(e))
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

    fun getQuickRemarks(imei: String) {

        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {

        }
    }
}