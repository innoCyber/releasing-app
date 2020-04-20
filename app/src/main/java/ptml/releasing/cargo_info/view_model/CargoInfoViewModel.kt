package ptml.releasing.cargo_info.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.form.FormMappers
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Event
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.cargo_info.model.FormDamage
import ptml.releasing.cargo_info.model.FormDataWrapper
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.configuration.models.ReleasingConfigureDeviceData
import ptml.releasing.damages.view.DamagesActivity
import ptml.releasing.form.FormSubmission
import ptml.releasing.form.FormType
import ptml.releasing.form.models.QuickRemark
import ptml.releasing.form.models.Voyage
import ptml.releasing.form.utils.Constants.VOYAGE_ID
import ptml.releasing.printer.model.Settings
import timber.log.Timber
import javax.inject.Inject

class CargoInfoViewModel @Inject constructor(
    val formMappers: FormMappers,
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

    fun getFormConfig(imei: String, findCargoResponse: FindCargoResponse?) {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val remarksMap = mutableMapOf<Int, QuickRemark>()
            val formConfig = repository.getFormConfigAsync().await()
            val remarks = repository.getQuickRemarkAsync(imei)?.await()
            for (remark in remarks?.data ?: mutableListOf()) {
                remarksMap[remark.id ?: return@launch] =
                    formMappers.quickRemarkMapper.mapFromModel(remark)
            }

            val form = if (shouldAddVoyage(findCargoResponse, formConfig)) {
                //add voyage form
                val formData = formConfig.data.toMutableList()
                formData.add(getVoyageForm())
                formConfig.copy(data = formData)
            } else {
                formConfig
            }
            val voyages = voyageRepository.getRecentVoyages().map {
                formMappers.voyagesMapper.mapFromModel(it)
            }.map {
                it.id to it
            }.toMap()

            val wrapper =
                FormDataWrapper(
                    remarksMap,
                    formMappers.configureDeviceMapper.mapFromModel(form),
                    voyages
                )
            withContext(appCoroutineDispatchers.main) {
                _formConfig.postValue(wrapper)
            }
        }
    }

    private fun shouldAddVoyage(
        findCargoResponse: FindCargoResponse?,
        formConfig: ConfigureDeviceResponse
    ): Boolean {
        return findCargoResponse?.isSuccess != true && containsNoVoyage(formConfig)
    }

    private fun containsNoVoyage(formConfig: ConfigureDeviceResponse): Boolean {
        val voyageForm = formConfig.data.filter { it.type == FormType.VOYAGE.type }
        return voyageForm.isEmpty()
    }

    fun getSettings() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val settings = repository.getSettings()
            withContext(appCoroutineDispatchers.main) {
                _printerSettings.postValue(settings)
            }
        }
    }

    private fun getVoyageForm(): ReleasingConfigureDeviceData {
        val data = ReleasingConfigureDeviceData(
            position = VOYAGE_ID,
            type = FormType.VOYAGE.type,
            title = "Select Voyage",
            required = true,
            editable = false,
            options = listOf(),
            dataValidation = ""
        )
        data.id = VOYAGE_ID

        return data
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

                val operator = getLoginUseCase.execute().badgeId

                formSubmission.submit()
                val configuration = repository.getSavedConfigAsync()
                val formSubmissionRequest = FormSubmissionRequest(
                    formSubmission.valuesList.map {
                        formMappers.formValueMapper.mapToModel(it)
                    },
                    formSubmission.selectionList.map {
                        formMappers.formSelectionMapper.mapToModel(it)
                    },
                    getDamages(),
                    configuration.cargoType.id, configuration.operationStep.id,
                    configuration.terminal.id, operator, cargoCode, cargoId,
                    formSubmission.selectedVoyage?.vesselName,
                    imei
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

    fun storeLastSelectedVoyage(change: Any?) {
        viewModelScope.launch {
            withContext(appCoroutineDispatchers.db) {
                (change as? Voyage)?.let {
                    Timber.d("Storing last selected voyage: $it")
                    voyageRepository.setLastSelectedVoyage(formMappers.voyagesMapper.mapToModel(it))
                }
            }
        }
    }
}