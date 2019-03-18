package ptml.releasing.setup

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
import ptml.releasing.app.base.BaseApiTest
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.device_configuration.viewmodel.DeviceConfigViewModel
import java.net.HttpURLConnection


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], manifest = Config.NONE)
class DeviceConfigurationTest:BaseApiTest(){

    companion object {
        private val SAMPLE_IMEI = "0000"

        private const val EXPECTED_ERROR_MESSAGE = "HTTP 502 Server Error"
    }

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Force tests to be executed synchronously


    private lateinit var activity: FragmentActivity
    private lateinit var viewModel: DeviceConfigViewModel


    @Before
    override fun setUp(){
        super.setUp()
        this.activity = Robolectric.setupActivity(FragmentActivity::class.java)
        this.viewModel = ViewModelProviders.of(this.activity, viewModelFactory).get(DeviceConfigViewModel::class.java)
    }



    @Test
    fun verifyDeviceSuccess() {
        // Prepare data
        this.mockHttpResponse("verifyDeviceIdSuccess.json", HttpURLConnection.HTTP_OK)

        // Pre-test
        kotlin.test.assertEquals(null, this.viewModel.baseLiveData.value, "Response should be null because network request has not started ")

        // Execute View Model
        this.viewModel.verifyDeviceId(SAMPLE_IMEI)


        // Assertions
        kotlin.test.assertEquals(true, this.viewModel.baseLiveData.value?.isSuccess, "Verify the response")
        kotlin.test.assertEquals(NetworkState.LOADED, this.viewModel.networkState.value, "Network state should be loaded")
    }

    @Test
    fun verifyDeviceFailureWithNetworkError(){

        // Prepare data
        this.mockHttpResponse("verifyDeviceIdSuccess.json", HttpURLConnection.HTTP_BAD_GATEWAY)

        // Pre-test
        kotlin.test.assertEquals(null, this.viewModel.baseLiveData.value, "Response should be null because network request has not started ")


        // Execute View Model
        this.viewModel.verifyDeviceId(SAMPLE_IMEI)


        // Assertions
        kotlin.test.assertEquals(null, this.viewModel.baseLiveData.value, "Response should be null")
        kotlin.test.assertEquals(EXPECTED_ERROR_MESSAGE, this.viewModel.networkState.value?.throwable?.message, "Error thrown")
    }

}