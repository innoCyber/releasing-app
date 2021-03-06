package ptml.releasing.app.utils.livedata

import androidx.lifecycle.*

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit) =
    observe(owner, Observer { observer.invoke(it) })

fun <T> LiveData<Event<T>>.observeEvent(owner: LifecycleOwner, observer: (T) -> Unit) =
    observe(owner, EventObserver<T> {
        observer.invoke(it)
    })

infix fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>) =
    Transformations.switchMap(this, func)

infix fun <X, Y> LiveData<X>.map(func: (X) -> LiveData<Y>) = Transformations.map(this, func)

infix fun <X, Y> LiveData<X>.mapFunc(func: (X) -> Y) = Transformations.map(this, func)

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this


