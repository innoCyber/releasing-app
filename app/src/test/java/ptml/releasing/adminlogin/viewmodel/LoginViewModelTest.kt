package ptml.releasing.adminlogin.viewmodel

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import ptml.releasing.R
import ptml.releasing.adminlogin.model.User
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.base.BaseTest
import ptml.releasing.data.getLoginFail
import ptml.releasing.data.getLoginSuccess
import ptml.releasing.data.getUser
import kotlin.test.assertNull

@Suppress("UNCHECKED_CAST")
class LoginViewModelTest : BaseTest() {

    private val repository: ReleasingRepository = mockk()
    private val user: User = mockk()
    private val viewModel by lazy { LoginViewModel(repository, dispatcher, updateChecker) }


    @Test
    fun `login with registered credentials is successful`() {
        every {
            user.username
        } returns getUser().username

        every {
            user.password
        } returns getUser().password


        coEvery {
            repository.loginAsync(any())
        } returns getLoginSuccess().toDeferredAsync() as Deferred<BaseResponse>


        assertNull(
            viewModel.getLoadNext().value,
            "DeviceConfigResponse should be null before a successful request"
        )

        viewModel.login(user.username, user.password)

        assertThat(viewModel.getLoadNext().value?.peekContent(), `is`(Unit))
    }

    @Test
    fun `login with unregistered credentials is unsuccessful`() {
        every {
            user.username
        } returns getUser().username

        every {
            user.password
        } returns getUser().password


        coEvery {
            repository.loginAsync(any())
        } returns getLoginFail().toDeferredAsync() as Deferred<BaseResponse>

        assertNull(
            viewModel.getErrorMessage().value,
            "Message should be null before a successful request"
        )


        viewModel.login(user.username, user.password)


        assertThat(viewModel.getErrorMessage().value?.peekContent(), `is`(getLoginFail().message))

    }

    @Test
    fun `login fails with input fields validation error`() {
        every {
            user.username
        } returns null
        every {
            user.password
        } returns null


        viewModel.login(null, null)

        assertThat(
            viewModel.getUsernameValidation().value?.peekContent(),
            `is`(R.string.username_empty)
        )
        assertThat(
            viewModel.getPasswordValidation().value?.peekContent(),
            `is`(R.string.password_empty)
        )
        assertNull(
            viewModel.getLoadNext().value?.peekContent(),
            "DeviceConfigResponse should be null"
        )
    }


}