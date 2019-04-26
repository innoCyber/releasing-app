package ptml.releasing.app.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.R
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import timber.log.Timber
import javax.inject.Inject

open class BaseViewModel @Inject constructor(
    protected val repository: Repository,
    protected val appCoroutineDispatchers: AppCoroutineDispatchers
) : ViewModel() {

    var compositeJob: Job? = null

    protected val _openBarCodeScanner = MutableLiveData<Unit>()
    protected val _isConfigured = MutableLiveData<Boolean>()
    protected val _operatorName = MutableLiveData<String?>()
    val isConfigured: LiveData<Boolean> = _isConfigured
    private val _savedOperatorName = MutableLiveData<String>()

    protected val _configuration = MutableLiveData<Configuration>()
    private val _openConfiguration = MutableLiveData<Unit>()


    val openConfiguration: LiveData<Unit> = _openConfiguration

    val operatorName: LiveData<String?> = _operatorName
    val openBarCodeScanner: LiveData<Unit> = _openBarCodeScanner
    val savedConfiguration: LiveData<Configuration> = _configuration
    val savedOperatorName: LiveData<String> = _savedOperatorName


    override fun onCleared() {
        when (compositeJob?.isCancelled) {
            false -> {
                compositeJob?.cancel()
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

                val configured =  repository.isConfiguredAsync()

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
                System.out.println("In here: ${e.localizedMessage}")
            }



    }

    fun isConfigured(): Boolean {
        return repository.isConfiguredAsync()
    }

    fun openBarCodeScanner(){
        _openBarCodeScanner.postValue(Unit)
    }


    fun saveOperatorName(name:String?){
        CoroutineScope(appCoroutineDispatchers.db).launch {
            repository.saveOperatorName(name)
            withContext(appCoroutineDispatchers.main){
                _savedOperatorName.postValue(name)
                _operatorName.postValue(name)
            }
        }
    }

    fun getOperatorName(){
        CoroutineScope(appCoroutineDispatchers.db).launch {
            val operator = repository.getOperatorName();
            withContext(appCoroutineDispatchers.main){
                _operatorName.postValue(operator)
            }
        }
    }

    open fun handleDeviceConfigured(configured: Boolean) {
        // classes that need to handle this will override
        Timber.d("Configured: %s", configured)
    }
}