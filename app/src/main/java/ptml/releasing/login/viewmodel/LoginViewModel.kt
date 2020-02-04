package ptml.releasing.login.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ptml.releasing.R
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.state.DataState
import ptml.releasing.app.data.domain.usecase.LoginUseCase
import ptml.releasing.app.data.domain.usecase.SetLoggedInUseCase
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Event
import ptml.releasing.app.utils.extensions.mapFunc
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kryptkode on 1/21/2020.
 */
class LoginViewModel @Inject constructor(
    private val setLoggedInUseCase: SetLoggedInUseCase,
    private val loginUseCase: LoginUseCase,
    private val context: Context,
    updateChecker: RemoteConfigUpdateChecker,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {


    val badgeId = MutableLiveData<String>()


    private val badgeIdError = MutableLiveData<String>()
    fun getBadgeIdErrorState(): LiveData<String> = badgeIdError

    val password = MutableLiveData<String>()

    private val passwordError = MutableLiveData<String>()
    fun getPasswordErrorState(): LiveData<String> = passwordError

    val hideKeyBoard = MutableLiveData<Boolean>()

    private val loginDataState = MutableLiveData<DataState<Unit>>()
    fun getLoginDataState(): LiveData<DataState<Unit>> = loginDataState

    private val goToSearchEvent = MutableLiveData<Event<Unit>>()
    fun getGoToSearchEvent(): LiveData<Event<Unit>> = goToSearchEvent

    val loading = loginDataState.mapFunc {
        it == DataState.Loading
    }

    private val goToReset = MutableLiveData<Event<Unit>>()
    fun getGoToReset(): LiveData<Event<Unit>> = goToReset

    fun handleLogin() {
        //hideKeyBoard
        hideKeyBoard.postValue(true)

        //clear errors
        badgeIdError.postValue("")
        passwordError.postValue("")

        val badgeId = badgeId.value
        val password = password.value

        if (!meetsAllConditions(badgeId, password)) {
            return
        }

        authenticate(badgeId, password)

    }

    @Suppress("NAME_SHADOWING")
    private fun authenticate(
        badgeId: String?,
        password: String?
    ) {
        viewModelScope.launch {
            loginDataState.postValue(DataState.Loading)
            try {

                val response = loginUseCase.execute(
                    LoginUseCase.Params(
                        badgeId ?: "",
                        password ?: "",
                        imei ?: ""
                    )
                )
                if (response?.success == true) {
                    loginUser()
                } else {
                    loginDataState.postValue(DataState.Error(response?.message))
                }
            } catch (e: Exception) {
                handleError(e, "Authentication error")
            }
        }
    }

    private fun handleError(e: Exception, message: String? = "Error") {
        Timber.e(e, message)
        loginDataState.postValue(DataState.Error(e.localizedMessage))
    }

    private fun loginUser() {
        viewModelScope.launch {
            try {
                setLoggedInUseCase.execute(SetLoggedInUseCase.Params(true))
                loginDataState.postValue(DataState.Success(Unit))
                navigateToNextScreen()
            } catch (e: Exception) {
                handleError(e, "Login User")
            }
        }
    }

    private fun navigateToNextScreen() {
        goToSearchEvent.postValue(Event(Unit))
    }

    fun handleReset() {
        //hideKeyBoard
        hideKeyBoard.postValue(true)
        goToReset.postValue(Event(Unit))

    }

    private fun meetsAllConditions(
        badgeId: String?,
        password: String?
    ): Boolean {

        if (badgeId.isNullOrEmpty() || password.isNullOrEmpty()) {
            if (badgeId.isNullOrEmpty()) {
                badgeIdError.postValue(context.getString(R.string.badge_id_required_message))
            }

            if (password.isNullOrEmpty()) {
                passwordError.postValue(context.getString(R.string.password_required_message))
            }
            return false
        }

        return true
    }


}