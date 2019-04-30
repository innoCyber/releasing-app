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
import ptml.releasing.cargo_info.model.FormDamage
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.damages.view.DamagesActivity
import ptml.releasing.printer.model.Settings
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class CargoInfoViewModel @Inject constructor(repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers) :
    BaseViewModel(repository, appCoroutineDispatchers) {

    private val _goBack = MutableLiveData<Boolean>()
    private val _formConfig = MutableLiveData<ConfigureDeviceResponse>()

    val goBack: LiveData<Boolean> = _goBack
    val formConfig: LiveData<ConfigureDeviceResponse> = _formConfig


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

    fun getFormConfig() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val formConfig = repository.getFormConfigAsync().await()
            withContext(appCoroutineDispatchers.main) {
                _formConfig.postValue(formConfig)
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

    fun submitForm(formSubmission: FormSubmission, cargoCode: String?) {
        if (_networkState.value == NetworkState.LOADING) return
        _networkState.postValue(NetworkState.LOADING)
        CoroutineScope(appCoroutineDispatchers.network).launch {
            try {
                formSubmission.submit()
                val configuration = repository.getSavedConfigAsync()

                val formSubmissionRequest = FormSubmissionRequest(
                    configuration.cargoType.id,
                    configuration.operationStep.id,
                    configuration.terminal.id,
                    cargoCode,
                    formSubmission.valuesList,
                    formSubmission.selectionList,
                    getDamages()
                )
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
}