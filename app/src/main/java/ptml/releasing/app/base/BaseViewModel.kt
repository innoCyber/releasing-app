package ptml.releasing.app.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

open class BaseViewModel : ViewModel() {
     var compositeJob: Job? = null




    override fun onCleared() {
        when(compositeJob?.isCancelled){
            false ->{
                compositeJob?.cancel()
            }
        }
        super.onCleared()
    }
}