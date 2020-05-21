package ptml.releasing.adminlogin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.R
import ptml.releasing.adminlogin.model.User
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Event
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {


    private val usernameValidation = MutableLiveData<Event<Int?>>()
    fun getUsernameValidation() : LiveData<Event<Int?>> = usernameValidation

    private val passwordValidation = MutableLiveData<Event<Int?>>()
    fun getPasswordValidation() : LiveData<Event<Int?>> = passwordValidation

    private val errorMessage = MutableLiveData<Event<String?>>()
    fun getErrorMessage(): LiveData<Event<String?>> = errorMessage

    private  val loadNext = MutableLiveData<Event<Unit>>()
    fun getLoadNext(): LiveData<Event<Unit>> = loadNext

    private val networkState = MutableLiveData<Event<NetworkState>>()
    fun getNetworkState():LiveData<Event<NetworkState>> = networkState

    fun login(username: String?, password: String?) {
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            if (username.isNullOrEmpty()) {
                usernameValidation.value = Event(R.string.username_empty)
            }

            if (password.isNullOrEmpty()) {
                passwordValidation.value = Event(R.string.password_empty)
            }

            return
        }

        usernameValidation.value = Event(null)
        passwordValidation.value = Event(null)

        val user = User(username, password)
        if (networkState.value?.peekContent() == NetworkState.LOADING) return
        networkState.postValue(Event(NetworkState.LOADING))
        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {
                val result = repository.loginAsync(user).await()
                withContext(dispatchers.main) {
                    Timber.d("Response: %s", result)
                    if (result.isSuccess) {
                        loadNext.postValue(Event(Unit))
                    } else {
                        errorMessage.postValue(Event(result.message))
                    }
                    networkState.postValue(Event(NetworkState.LOADED))
                }
            } catch (it: Throwable) {
                Timber.e(it)
                networkState.postValue(Event(NetworkState.error(it)))
            }
        }
    }

}