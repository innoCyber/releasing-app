package ptml.releasing.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.R
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.auth.model.User
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class LoginViewModel @Inject constructor(
        var repository: Repository,
        var appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel() {


    val usernameValidation = MutableLiveData<Int>()
    val passwordValidation = MutableLiveData<Int>()
    val response = MutableLiveData<BaseResponse>()

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
                    if (result.data.isNotEmpty()) {
                        val data = result.data[0]
                        response.postValue(result.data[0])
                        Timber.d("Response: %s", data)
                        networkState.postValue(NetworkState.LOADED)
                    } else {
                        networkState.postValue(NetworkState.error(Exception("Response received was unexpected")))
                    }
                }
            } catch (it: Throwable) {
                Timber.e(it)
                networkState.postValue(NetworkState.error(it))
            }
        }
    }

}