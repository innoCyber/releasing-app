package ptml.releasing.login.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ptml.releasing.R
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.model.login.OperationStep
import ptml.releasing.app.data.domain.model.login.Terminal
import ptml.releasing.app.data.domain.state.DataState
import ptml.releasing.app.data.domain.usecase.GetAdminOptionsUseCase
import ptml.releasing.app.data.domain.usecase.LoginUseCase
import ptml.releasing.app.data.domain.usecase.SetLoggedInUseCase
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Constants.INVALID_ID
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
    private val adminOptionsUseCase: GetAdminOptionsUseCase,
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

    val selectedOperationType = MutableLiveData<String>()

    private val operationTypeError = MutableLiveData<String>()
    fun getOperationTypeErrorState(): LiveData<String> = operationTypeError

    private val operationTypeList = mutableListOf<OperationStep>()
    private val operationTypeNameList = MutableLiveData<List<String>>()
    fun getOperationTypeList(): LiveData<List<String>> = operationTypeNameList

    val selectedTerminal = MutableLiveData<String>()

    private val terminalError = MutableLiveData<String>()
    fun getTerminalErrorState(): LiveData<String> = terminalError

    private val terminalsList = mutableListOf<Terminal>()
    private val terminalNameList = MutableLiveData<List<String>>()
    fun getTerminalList(): LiveData<List<String>> = terminalNameList

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
        terminalError.postValue("")
        operationTypeError.postValue("")

        val badgeId = badgeId.value
        val password = password.value
        val operationType = selectedOperationType.value
        val terminal = selectedTerminal.value

        if (!meetsAllConditions(badgeId, password, operationType, terminal)) {
            return
        }

        authenticate(badgeId, password, operationType, terminal)

    }

    @Suppress("NAME_SHADOWING")
    private fun authenticate(
        badgeId: String?,
        password: String?,
        operationType: String?,
        terminal: String?
    ) {
        viewModelScope.launch {
            loginDataState.postValue(DataState.Loading)
            try {
                val operationType = getOperationTypeId(operationType ?: "")
                val terminal = getTerminalId(terminal ?: "")
                val response = loginUseCase.execute(
                    LoginUseCase.Params(
                        badgeId ?: "",
                        password ?: "",
                        imei ?: "",
                        operationType,
                        terminal
                    )
                )
                if (response?.success == true) {
                    //download the form
                    downloadForm(operationType.id ?: INVALID_ID, terminal.id ?: INVALID_ID)
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

    private fun downloadForm(operationType: Int, terminal: Int) {
        /*viewModelScope.launch {
            loginDataState.postValue(DataState.Loading)
            try {
                val response = formDownloadUseCase.execute(
                    DownloadFormUseCase.Params(
                        imei ?: "",
                        operationType,
                        terminal
                    )
                )
                if (response.success == true) {
                    //TODO: Store session
                    loginUser()
                } else {
                    loginDataState.postValue(DataState.Error(response.message))
                }

            } catch (e: Exception) {
                handleError(e, "Download form")
            }
        }*/
        loginUser()
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
        password: String?,
        operationType: String?,
        terminal: String?
    ): Boolean {

        if (badgeId.isNullOrEmpty() || password.isNullOrEmpty() || operationType.isNullOrEmpty() || terminal.isNullOrEmpty()) {
            if (badgeId.isNullOrEmpty()) {
                badgeIdError.postValue(context.getString(R.string.badge_id_required_message))
            }

            if (password.isNullOrEmpty()) {
                passwordError.postValue(context.getString(R.string.password_required_message))
            }

            if (operationType.isNullOrEmpty()) {
                operationTypeError.postValue(context.getString(R.string.operation_type_required_message))
            }

            if (terminal.isNullOrEmpty()) {
                terminalError.postValue(context.getString(R.string.terminal_required_message))
            }

            return false
        }

        return true
    }

    fun fetchAdminOptions(imei: String) {
        viewModelScope.launch {
            loginDataState.postValue(DataState.Loading)
            try {
                val result = adminOptionsUseCase.execute(GetAdminOptionsUseCase.Params(imei))
                if (result.success == true) {
                    result.operationSteps?.let {
                        operationTypeList.clear()
                        operationTypeList.addAll(it)
                    }

                    result.terminals?.let {
                        terminalsList.clear()
                        terminalsList.addAll(it)
                    }

                    setTerminals()

                    setOperationType()
                    loginDataState.postValue(DataState.Success(Unit))
                } else {
                    loginDataState.postValue(DataState.Error(result.message))
                }
            } catch (e: Exception) {
                handleError(e, "Error getting admin options")
            }
        }

    }

    private fun setTerminals() {
        val terminalNames =
            terminalsList.map { it.value ?: context.getString(R.string.no_value_from_server) }
        terminalNameList.postValue(terminalNames)

        terminalNames.apply {
            if (isNotEmpty()) {
                selectedTerminal.postValue(get(0))
            }
        }
    }

    private fun setOperationType() {
        val operationTypeNames =
            operationTypeList.map { it.value ?: context.getString(R.string.no_value_from_server) }
        operationTypeNameList.postValue(operationTypeNames)

        operationTypeNames.apply {
            if (isNotEmpty()) {
                selectedOperationType.postValue(get(0))
            }
        }
    }

    private fun getTerminalId(terminal: String): Terminal {
        val filteredList = terminalsList.filter {
            it.value == terminal
        }
        return if (filteredList.isNotEmpty()) {
            filteredList[0]
        } else {
            INVALID_TERMINAL
        }
    }

    private fun getOperationTypeId(operationType: String): OperationStep {
        val filteredList = operationTypeList.filter {
            it.value == operationType
        }
        return if (filteredList.isNotEmpty()) {
            filteredList[0]
        } else {
            INVALID_OPERATION_STEP
        }
    }

    companion object {
        val INVALID_OPERATION_STEP = OperationStep(INVALID_ID, INVALID_ID, "")
        val INVALID_TERMINAL = Terminal(INVALID_ID, INVALID_ID, "")
    }


}