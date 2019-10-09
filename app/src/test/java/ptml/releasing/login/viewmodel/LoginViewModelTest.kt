package ptml.releasing.login.viewmodel

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
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


        assertNull(viewModel.loadNext.value, "DeviceConfigResponse should be null before a successful request")

        viewModel.login(user.username, user.password)

        assertThat(viewModel.loadNext.value, `is`(Unit))
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

        assertNull(viewModel.errorMessage.value, "Message should be null before a successful request")


        viewModel.login(user.username, user.password)


        assertThat(viewModel.errorMessage.value, `is`(getLoginFail().message))

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

        assertThat(viewModel.usernameValidation.value, `is`(R.string.username_empty))
        assertThat(viewModel.passwordValidation.value, `is`(R.string.password_empty))
        assertNull(viewModel.loadNext.value, "DeviceConfigResponse should be null")
    }


}