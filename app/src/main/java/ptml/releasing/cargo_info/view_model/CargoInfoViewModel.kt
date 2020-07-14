package ptml.releasing.cargo_info.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.form.FormMappers
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Constants
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
import ptml.releasing.images.worker.ImageUploadWorker
import ptml.releasing.printer.model.Settings
import timber.log.Timber
import javax.inject.Inject

class CargoInfoViewModel @Inject constructor(
    private val context: Context,
    val formMappers: FormMappers,
    repository: Repository,
    dispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, dispatchers) {

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

    private val imagesCountState = MutableLiveData<Int>()
    fun getImagesCountState(): LiveData<Int> = imagesCountState


    fun goBack() {
        _goBack.postValue(Event(true))
    }


    fun getFormConfig(imei: String, findCargoResponse: FindCargoResponse?) {
        compositeJob = CoroutineScope(dispatchers.db).launch {

            try {
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

                val voyages = try {
                    voyageRepository.getRecentVoyages().map {
                        formMappers.voyagesMapper.mapFromModel(it)
                    }.map {
                        it.id to it
                    }.toMap()
                } catch (e: Exception) {
                    mapOf<Int, Voyage>()
                }


                val wrapper =
                    FormDataWrapper(
                        remarksMap,
                        formMappers.configureDeviceMapper.mapFromModel(form),
                        voyages
                    )
                withContext(dispatchers.main) {
                    _formConfig.postValue(wrapper)
                }
            } catch (e: Exception) {
                Timber.e(e)
                _networkState.postValue(Event(NetworkState.error(e)))
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

    fun onPrintBarcode() {
        compositeJob = CoroutineScope(dispatchers.db).launch {
            val settings = repository.getPrinterSettings()
            withContext(dispatchers.main) {
                _printerSettings.postValue(settings)
            }
        }
    }

    fun onPrintDamages() {
        viewModelScope.launch {
            val settings = repository.getPrinterSettings()
            settings.labelCpclData = Constants.DEFAULT_MULTILINE_PRINTER_SETTINGS
            _printerSettings.postValue(settings)
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
        findCargoResponse: FindCargoResponse?,
        cargoCode: String?,
        imei: String?
    ) {
        if (_networkState.value?.peekContent() == NetworkState.LOADING) return
        _networkState.postValue(Event(NetworkState.LOADING))
        CoroutineScope(dispatchers.network).launch {
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
                    configuration.cargoType.id,
                    configuration.operationStep.id,
                    configuration.terminal.id,
                    operator,
                    cargoCode,
                    findCargoResponse?.mrkNumber,
                    findCargoResponse?.grimaldiContainer,
                    findCargoResponse?.cargoId,
                    getPhotoNames(cargoCode),
                    formSubmission.selectedVoyage?.id,
                    imei
                )
                val result = repository.uploadData(formSubmissionRequest).await()

                withContext(dispatchers.main) {
                    if (result.isSuccess) {
                        //schedule upload images
                        val workRequest =
                            ImageUploadWorker.createWorkRequest(
                                cargoCode ?: "",
                                configuration.operationStep.id ?: return@withContext,
                                findCargoResponse?.cargoId ?: 0,
                                configuration.cargoType.id ?: 0
                            )
                        repository.addWorkerId(cargoCode ?: "", workRequest.id.toString())
                        WorkManager.getInstance(context).enqueue(workRequest)
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

    fun getImagesCount(cargoCode: String?) {
        Timber.d("Get image count $cargoCode")
        CoroutineScope(dispatchers.db).launch {
            val count = repository.getImages(cargoCode ?: return@launch).size
            Timber.d("Image count: $count")
            imagesCountState.postValue(count)
        }
    }

    private suspend fun getPhotoNames(cargoCode: String?): List<String> {
        return repository.getImages(cargoCode ?: return emptyList()).values.map {
            it.name ?: ""
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
            withContext(dispatchers.db) {
                (change as? Voyage)?.let {
                    Timber.d("Storing last selected voyage: $it")
                    voyageRepository.setLastSelectedVoyage(formMappers.voyagesMapper.mapToModel(it))
                }
            }
        }
    }
}