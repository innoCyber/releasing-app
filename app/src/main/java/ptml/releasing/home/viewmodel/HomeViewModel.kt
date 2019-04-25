package ptml.releasing.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(repository, appCoroutineDispatchers) {
    private val _openDownloadDamages = MutableLiveData<Boolean>()
    private val _openSearch = MutableLiveData<Boolean>()


    val openDownloadDamages: LiveData<Boolean> = _openDownloadDamages
    val openSearch: LiveData<Boolean> = _openSearch








    fun openSearch() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val configured = repository.isConfiguredAsync()
            withContext(appCoroutineDispatchers.main) {
                _openSearch.postValue(configured)
            }
        }
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