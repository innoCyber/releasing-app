package ptml.releasing.login.viewmodel

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import org.junit.Test
import ptml.releasing.R
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.login.model.User
import ptml.releasing.base.BaseTest
import ptml.releasing.data.getLoginFail
import ptml.releasing.data.getLoginSuccess
import ptml.releasing.data.getUser
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LoginViewModelTest : BaseTest() {

    private val repository: ReleasingRepository = mockk()
    private val user: User = mockk()
    private val viewModel by lazy { LoginViewModel(repository, dispatcher) }


    @Test
    fun `successful login`() {
        every {
            user.username
        } returns getUser().username

        every {
            user.password
        } returns getUser().password


        coEvery {
            repository.loginAsync(any())
        } returns getLoginSuccess().toDeferredAsync() as Deferred<BaseResponse>


        assertNull(viewModel.response.value, "DeviceConfigResponse should be null before a successful request")

        viewModel.login(user.username, user.password)

        assertEquals(getLoginSuccess(), viewModel.response.value, "Login is successful")
    }

    @Test
    fun `unsuccessful login`() {
        every {
            user.username
        } returns getUser().username

        every {
            user.password
        } returns getUser().password


        coEvery {
            repository.loginAsync(any())
        } returns getLoginFail().toDeferredAsync() as Deferred<BaseResponse>

        assertNull(viewModel.response.value, "DeviceConfigResponse should be null before a successful request")

        viewModel.login(user.username, user.password)


        assertEquals(getLoginFail(), viewModel.response.value)

    }

    @Test
    fun `login with  validation error`() {
        every {
            user.username
        } returns null
        every {
            user.password
        } returns null



        viewModel.login(null, null)

        assertEquals(R.string.username_empty, viewModel.usernameValidation.value)
        assertEquals(R.string.password_empty, viewModel.passwordValidation.value)
        assertNull(viewModel.response.value, "DeviceConfigResponse should be null")

    }


}