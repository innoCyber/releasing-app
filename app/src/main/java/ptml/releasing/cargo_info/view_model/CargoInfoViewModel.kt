package ptml.releasing.cargo_info.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.printer.model.Settings
import javax.inject.Inject

class CargoInfoViewModel @Inject constructor(repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers)
    : BaseViewModel(repository, appCoroutineDispatchers) {

    private val _goBack = MutableLiveData<Boolean>()
    private val _formConfig = MutableLiveData<ConfigureDeviceResponse>()

    val goBack: LiveData<Boolean> = _goBack
    val formConfig: LiveData<ConfigureDeviceResponse> = _formConfig


    private val _printerSettings = MutableLiveData<Settings>()
    val printerSettings: LiveData<Settings> = _printerSettings

    fun goBack(){
        _goBack.postValue(true)
    }

    fun getFormConfig(){
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
           val formConfig =  repository.getFormConfigAsync().await()
            withContext(appCoroutineDispatchers.main){
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
}