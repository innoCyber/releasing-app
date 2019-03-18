package ptml.releasing.auth.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import io.reactivex.Scheduler
import ptml.releasing.R
import ptml.releasing.app.data.Repository
import ptml.releasing.auth.model.User
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.di.modules.rx.OBSERVER_ON
import ptml.releasing.app.di.modules.rx.SUBSCRIBER_ON
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LoginViewModel @Inject constructor(
    var repository: Repository,
    @param:Named(SUBSCRIBER_ON) var subscriberOn: Scheduler,
    @param:Named(OBSERVER_ON) var observerOn: Scheduler
) : BaseViewModel() {


    val usernameValidation = MutableLiveData<Int>()
    val passwordValidation = MutableLiveData<Int>()
    val response = MutableLiveData<BaseResponse>()

    val networkState = MutableLiveData<NetworkState>()


    fun login(username:String?, password:String?){
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            if(TextUtils.isEmpty(username)){
                usernameValidation.value = R.string.username_empty
            }

            if(TextUtils.isEmpty(password)){
                passwordValidation.value = R.string.password_empty
            }
            return
        }

        usernameValidation.value = null
        passwordValidation.value = null

        val user = User(username, password)
        if(networkState.value == NetworkState.LOADING) return
        networkState.postValue(NetworkState.LOADING)
        disposable.add(repository.login(user)
            .subscribeOn(subscriberOn)
            .observeOn(observerOn)
            .subscribe({
                response.postValue(it)
                networkState.postValue(NetworkState.LOADED)
            }, {
                Timber.e(it)
                networkState.postValue(NetworkState.error(it))
            }))
    }


}