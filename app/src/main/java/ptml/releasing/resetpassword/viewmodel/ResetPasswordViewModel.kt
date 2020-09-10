package ptml.releasing.resetpassword.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ptml.releasing.R
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.state.DataState
import ptml.releasing.app.data.domain.usecase.ResetPasswordUseCase
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.livedata.Event
import ptml.releasing.app.utils.livedata.mapFunc
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */

class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val context: Context,
    updateChecker: RemoteConfigUpdateChecker,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    val badgeId = MutableLiveData<String>()

    private val badgeIdError = MutableLiveData<String>()
    fun getBadgeIdErrorState(): LiveData<String> = badgeIdError

    val newPassword = MutableLiveData<String>()

    private val newPasswordError = MutableLiveData<String>()
    fun getNewPasswordErrorState(): LiveData<String> = newPasswordError

    val confirmPassword = MutableLiveData<String>()

    private val confirmPasswordError = MutableLiveData<String>()
    fun getConfirmPasswordErrorState(): LiveData<String> = confirmPasswordError


    val hideKeyBoard = MutableLiveData<Boolean>()

    private val forgotPasswordDataState = MutableLiveData<DataState<Unit>>()
    fun getResetPasswordDataState(): LiveData<DataState<Unit>> = forgotPasswordDataState

    private val exit = MutableLiveData<Event<Unit>>()
    fun getExitState(): LiveData<Event<Unit>> = exit

    val loading = forgotPasswordDataState.mapFunc {
        it == DataState.Loading
    }


    fun handleReset() {
        //hideKeyBoard
        hideKeyBoard.postValue(true)

        //clear errors
        badgeIdError.postValue("")
        newPasswordError.postValue("")
        confirmPasswordError.postValue("")

        val badgeId = badgeId.value
        val password = newPassword.value
        val passwordConfirm = confirmPassword.value

        if (!meetsAllConditions(badgeId, password, passwordConfirm)) {
            return
        }

        resetPassword(badgeId, password)
    }

    fun handleBackNavigation() {
        exit.postValue(Event(Unit))
    }

    private fun resetPassword(
        badgeId: String?,
        password: String?
    ) {

        viewModelScope.launch {
            forgotPasswordDataState.postValue(DataState.Loading)
            try {
                val response = resetPasswordUseCase.execute(
                    ResetPasswordUseCase.Params(
                        badgeId ?: "",
                        password ?: "",
                        imei ?: ""
                    )
                )
                Timber.d("Response: $response")
                if (response?.success == true) {
                    forgotPasswordDataState.postValue(DataState.Success(Unit))
                } else {
                    forgotPasswordDataState.postValue(DataState.Error(response?.message))
                }
            } catch (e: Exception) {
                Timber.e(e, "Authentication error")
                forgotPasswordDataState.postValue(DataState.Error(e.localizedMessage))
            }
        }
    }

    private fun meetsAllConditions(
        badgeId: String?,
        password: String?,
        passwordConfirm: String?
    ): Boolean {

        if (badgeId.isNullOrEmpty() ||
            password.isNullOrEmpty() ||
            passwordConfirm.isNullOrEmpty() ||
            password != passwordConfirm
        ) {

            if (badgeId.isNullOrEmpty()) {
                badgeIdError.postValue(context.getString(R.string.badge_id_required_message))
            }

            if (password.isNullOrEmpty()) {
                newPasswordError.postValue(context.getString(R.string.password_required_message))
            } else if (password != passwordConfirm) {
                newPasswordError.postValue(context.getString(R.string.password_no_match_message))
                confirmPasswordError.postValue(context.getString(R.string.password_no_match_message))
            }

            if (passwordConfirm.isNullOrEmpty()) {
                confirmPasswordError.postValue(context.getString(R.string.password_required_message))
            } else if (passwordConfirm != password) {
                newPasswordError.postValue(context.getString(R.string.password_no_match_message))
                confirmPasswordError.postValue(context.getString(R.string.password_no_match_message))
            }

            return false
        }

        return true
    }


}