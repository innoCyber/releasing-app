package ptml.releasing.find_cargo.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

class FindCargoViewModel @Inject constructor(repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers)
    : BaseViewModel(repository, appCoroutineDispatchers) {


    private val _goBack = MutableLiveData<Boolean>()

    val goBack: LiveData<Boolean> = _goBack

    fun goBack(){
        _goBack.postValue(true)
    }
}