package ptml.releasing.cargo_info.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.form.FormSubmission
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.SingleLiveEvent
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.cargo_info.model.FormDamage
import ptml.releasing.cargo_info.model.FormDataWrapper
import ptml.releasing.cargo_info.model.FormSubmissionRequest
import ptml.releasing.damages.view.DamagesActivity
import ptml.releasing.images.worker.ImageUploadWorker
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemark
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class CargoInfoViewModel @Inject constructor(
    private val context: Context,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

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

    val errorMessage: LiveData<String> = _errorMessage
    val submitSuccess: LiveData<Unit> = _submitSuccess

    private val imagesCountState = MutableLiveData<Int>()
    fun getImagesCountState(): LiveData<Int> = imagesCountState


    fun goBack() {
        _goBack.postValue(true)
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
                    configuration.terminal.id, operator, cargoCode, cargoId,
                    getPhotoNames(cargoCode),
                    imei
                )
                val result = repository.uploadData(formSubmissionRequest).await()

                withContext(appCoroutineDispatchers.main) {
                    if (result.isSuccess) {
                        _submitSuccess.postValue(Unit)
                        //schedule upload images
                        val workRequest =
                            ImageUploadWorker.createWorkRequest(cargoCode ?: return@withContext)
                        repository.addWorkerId(cargoCode, workRequest.id.toString())
                        WorkManager.getInstance(context).enqueue(workRequest)
                    } else {
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

    fun getImagesCount(cargoCode: String?){
        Timber.d("Get image count $cargoCode")
        CoroutineScope(appCoroutineDispatchers.db).launch {
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
}