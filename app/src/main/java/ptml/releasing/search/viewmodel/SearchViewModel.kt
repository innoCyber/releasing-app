package ptml.releasing.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(repository, appCoroutineDispatchers) {

    private val _goBack = MutableLiveData<Unit>()
    private val _verify = MutableLiveData<Unit>()

    val goBack:LiveData<Unit> = _goBack
    val verify:LiveData<Unit> = _verify

    fun goBack(){
        _goBack.postValue(Unit)
    }

    fun verify(){
        _verify.postValue(Unit)
    }


}