package ptml.releasing.login.viewmodel

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.R
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.login.model.User
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(
        repository: Repository,
        appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(repository, appCoroutineDispatchers) {


    val usernameValidation = MutableLiveData<Int>()
    val passwordValidation = MutableLiveData<Int>()
    val errorMessage = MutableLiveData<String>()
    val loadNext= MutableLiveData<Unit>()

    val networkState = MutableLiveData<NetworkState>()


    fun login(username: String?, password: String?) {
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            if (username.isNullOrEmpty()) {
                usernameValidation.value = R.string.username_empty
            }

            if (password.isNullOrEmpty()) {
                passwordValidation.value = R.string.password_empty
            }

            return
        }

        usernameValidation.value = null
        passwordValidation.value = null

        val user = User(username, password)
        if (networkState.value == NetworkState.LOADING) return
        networkState.postValue(NetworkState.LOADING)
        compositeJob = CoroutineScope(appCoroutineDispatchers.network).launch {
            try {
                val result = repository.loginAsync(user).await()
                withContext(appCoroutineDispatchers.main) {
                    Timber.d("Response: %s", result)
                    if(result.isSuccess){
                        loadNext.postValue(Unit)
                    }else{
                        errorMessage.postValue(result.message)
                    }
                    networkState.postValue(NetworkState.LOADED)
                }
            } catch (it: Throwable) {
                Timber.e(it)
                networkState.postValue(NetworkState.error(it))
            }
        }
    }

}