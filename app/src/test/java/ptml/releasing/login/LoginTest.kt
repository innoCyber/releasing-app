package ptml.releasing.login

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ptml.releasing.R
import ptml.releasing.auth.viewmodel.LoginViewModel
import ptml.releasing.app.base.BaseApiTest
import ptml.releasing.auth.model.User
import java.net.HttpURLConnection


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], manifest = Config.NONE)
class LoginTest : BaseApiTest() {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Force tests to be executed synchronously


    private lateinit var user: User
    private lateinit var activity: FragmentActivity
    private lateinit var viewModel: LoginViewModel

    companion object {
        const val PASSWORD = "1234"
        const val USERNAME = "paul"

        private const val EXPECTED_EXCEPTION_MESSAGE = "HTTP 504 Server Error"
    }

    @Before
    override fun setUp() {
        super.setUp()
        this.activity = Robolectric.setupActivity(FragmentActivity::class.java)
        this.viewModel = ViewModelProviders.of(this.activity, viewModelFactory).get(LoginViewModel::class.java)
        this.user = Mockito.mock(User::class.java)
    }

    @Test
    fun loginSuccess() {
        // Prepare data
        Mockito.`when`(user.password).thenReturn(PASSWORD)
        Mockito.`when`(user.username).thenReturn(USERNAME)
        this.mockHttpResponse("loginSuccess.json", HttpURLConnection.HTTP_OK)

        kotlin.test.assertNull(viewModel.response.value, "Response should be null before a successful request")

        viewModel.login(user.username, user.password)


        kotlin.test.assertNotNull(viewModel.response, "Response should not be null, successful was successful request")
        kotlin.test.assertEquals(true, viewModel.response.value?.isSuccess)

    }


    @Test
    fun loginFailure() {
        // Prepare data
        Mockito.`when`(user.password).thenReturn(PASSWORD)
        Mockito.`when`(user.username).thenReturn(USERNAME)
        this.mockHttpResponse("failure.json", HttpURLConnection.HTTP_OK)

        kotlin.test.assertNull(viewModel.response.value, "Response should be null before a successful request")

        viewModel.login(user.username, user.password)


        kotlin.test.assertNotNull(viewModel.response, "Response should not be null,  was a successful request")
        kotlin.test.assertEquals(false, viewModel.response.value?.isSuccess)
        kotlin.test.assertEquals("An error occurred", viewModel.response.value?.message)

    }


    @Test
    fun loginFailureException() {
        // Prepare data
        Mockito.`when`(user.password).thenReturn(PASSWORD)
        Mockito.`when`(user.username).thenReturn(USERNAME)
        this.mockHttpResponse("failure.json", HttpURLConnection.HTTP_GATEWAY_TIMEOUT)

        // Pre-test
        kotlin.test.assertNull(this.viewModel.response.value, "Response should be null before a successful request")


        // Execute View Model
        viewModel.login(user.username, user.password)


        // Assertions
        kotlin.test.assertNull(this.viewModel.response.value)
        kotlin.test.assertEquals(
            EXPECTED_EXCEPTION_MESSAGE,
            this.viewModel.networkState.value?.throwable?.message,
            "Error thrown"
        )
    }


    @Test
    fun validationError() {
        // Prepare data
        Mockito.`when`(user.password).thenReturn("")
        Mockito.`when`(user.username).thenReturn("")
        this.mockHttpResponse("loginSuccess.json", HttpURLConnection.HTTP_OK)

        kotlin.test.assertNull(
            viewModel.usernameValidation.value,
            " Username validation should be null before a successful request"
        )
        kotlin.test.assertNull(
            viewModel.passwordValidation.value,
            " Password validation should be null before a successful request"
        )

        viewModel.login(user.username, user.password)


        kotlin.test.assertNotNull(viewModel.usernameValidation, "Username validation should not be null")
        kotlin.test.assertNotNull(viewModel.passwordValidation, "Password validation should not be null")
        kotlin.test.assertEquals(R.string.username_empty, viewModel.usernameValidation.value)
        kotlin.test.assertEquals(R.string.password_empty, viewModel.passwordValidation.value)

    }


}