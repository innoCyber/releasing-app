package ptml.releasing.app.base

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