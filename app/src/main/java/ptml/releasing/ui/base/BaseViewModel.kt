package ptml.releasing.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {
    val disposable = CompositeDisposable()




    override fun onCleared() {
        if(!disposable.isDisposed){
            disposable.dispose()
        }
        super.onCleared()
    }
}