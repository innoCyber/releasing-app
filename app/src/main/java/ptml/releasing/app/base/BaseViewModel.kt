package ptml.releasing.app.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.admin_configuration.models.Configuration
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import timber.log.Timber
import javax.inject.Inject

open class BaseViewModel @Inject constructor(
    protected val repository: Repository,
    protected val appCoroutineDispatchers: AppCoroutineDispatchers
) : ViewModel() {

    var compositeJob: Job? = null

    private val _isConfigured = MutableLiveData<Boolean>()
    val isConfigured: LiveData<Boolean> = _isConfigured

    private val _configuration = MutableLiveData<Configuration>()

    val savedConfiguration: LiveData<Configuration> = _configuration


    override fun onCleared() {
        when (compositeJob?.isCancelled) {
            false -> {
                compositeJob?.cancel()
            }
        }
        super.onCleared()
    }




    fun getSavedConfig() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            try {
                Timber.d("Checking if there is a saved configuration")
                val configured = repository.isConfiguredAsync().await()
                withContext(appCoroutineDispatchers.main) {
                    _isConfigured.postValue(configured)
                    handleDeviceConfigured(configured)
                }
                if (configured) {
                    Timber.d("Configuration was saved before, getting the configuration")
                    val config = repository.getSavedConfigAsync().await()
                    Timber.d("Configuration gotten: %s", config)
                    withContext(appCoroutineDispatchers.main) {
                        _configuration.postValue(config)

                    }
                }

            } catch (e: Throwable) {
                Timber.e(e)
                System.out.println("In here: ${e.localizedMessage}")
            }
        }


    }

    open fun handleDeviceConfigured(configured: Boolean) {
        // classes that need to handle this will override
        Timber.d("Configured: %s", configured)
    }
}