package ptml.releasing.ui.configuration

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ptml.releasing.base.BaseApiTest
import java.net.HttpURLConnection

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], manifest = Config.NONE)
class ConfigurationViewModelTest : BaseApiTest() {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Force tests to be executed synchronously

    private lateinit var activity: FragmentActivity
    private lateinit var viewModel: ConfigurationViewModel

    companion object {
        const val IMEI = "1234"

        private const val EXPECTED_EXCEPTION_MESSAGE = "HTTP 504 Server Error"
    }


    @Before
    override fun setUp() {
        super.setUp()
        this.activity = Robolectric.setupActivity(FragmentActivity::class.java)
        this.viewModel = ViewModelProviders.of(this.activity, viewModelFactory).get(ConfigurationViewModel::class.java)
    }


    @Test
    fun getConfigSuccess() {
        // Prepare data
        this.mockHttpResponse("loginSuccess.json", HttpURLConnection.HTTP_OK)

        kotlin.test.assertNull(viewModel.configResponse.value, "Response should be null before a successful request")

        viewModel.getConfig(IMEI)

        kotlin.test.assertNotNull(viewModel.configResponse.value, "Response should not be null, request was successful")
        kotlin.test.assertEquals(true, viewModel.configResponse.value?.isSuccess)

    }


    @Test
    fun getConfigFailure() {
        // Prepare data
        this.mockHttpResponse("failure.json", HttpURLConnection.HTTP_OK)

        kotlin.test.assertNull(viewModel.configResponse.value, "Response should be null before a successful request")

        viewModel.getConfig(IMEI)

        kotlin.test.assertNotNull(viewModel.configResponse, "Response should not be null, request was successful ")
        kotlin.test.assertEquals(false, viewModel.configResponse.value?.isSuccess)
        kotlin.test.assertEquals("An error occurred", viewModel.configResponse.value?.message)


    }


    @Test
    fun getConfigFailureException() {
        // Prepare data
        this.mockHttpResponse("failure.json", HttpURLConnection.HTTP_GATEWAY_TIMEOUT)

        // Pre-test
        kotlin.test.assertNull(viewModel.configResponse.value, "Response should be null before a successful request")


        // Execute View Model
        viewModel.getConfig(IMEI)


        // Assertions
        kotlin.test.assertNull(this.viewModel.configResponse.value)
        kotlin.test.assertEquals(
            EXPECTED_EXCEPTION_MESSAGE,
            this.viewModel.networkState.value?.throwable?.message,
            "Error thrown"
        )
    }



}