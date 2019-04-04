package ptml.releasing.admin_config.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

class AdminConfigViewModel @Inject constructor(repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers) :
    BaseViewModel(repository, appCoroutineDispatchers) {

    private val _openConfiguration = MutableLiveData<Boolean>()
    private val _openDownloadDamages = MutableLiveData<Boolean>()

    val openConfiguration: LiveData<Boolean> = _openConfiguration
    val openDownloadDamages: LiveData<Boolean> = _openDownloadDamages


    fun openConfiguration() {
        _openConfiguration.postValue(true)
    }


    fun openDownloadDamages() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(appCoroutineDispatchers.main) {
                _openDownloadDamages.postValue(configured)
            }
        }
    }
}