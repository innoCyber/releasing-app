package ptml.releasing.ui.setup

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
import ptml.releasing.db.models.response.base.BaseResponse
import ptml.releasing.utils.NetworkState
import java.net.HttpURLConnection


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], manifest = Config.NONE)
class SetupActivityViewModelApiTest:BaseApiTest(){

    companion object {
        private val SAMPLE_IMEI = "0000"

        private val EXPECTED_RESPONSE = BaseResponse("", true)

        private const val EXPECTED_ERROR_MESSAGE = "HTTP 502 Server Error"
    }

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Force tests to be executed synchronously


    private lateinit var activity: FragmentActivity
    private lateinit var viewModel: SetupActivityViewModel


    @Before
    override fun setUp(){
        super.setUp()
        this.activity = Robolectric.setupActivity(FragmentActivity::class.java)
        this.viewModel = ViewModelProviders.of(this.activity, viewModelFactory).get(SetupActivityViewModel::class.java)
    }



    @Test
    fun getUser_whenSuccess() {
        // Prepare data
        this.mockHttpResponse("verifyDeviceIdSuccess.json", HttpURLConnection.HTTP_OK)

        // Pre-test
        kotlin.test.assertEquals(null, this.viewModel.baseLiveData.value, "Response should be null because network request has not started ")

        // Execute View Model
        this.viewModel.verifyDeviceId(SAMPLE_IMEI)


        // Assertions
        kotlin.test.assertEquals(EXPECTED_RESPONSE, this.viewModel.baseLiveData.value, "Verify the response")
        kotlin.test.assertEquals(NetworkState.LOADED, this.viewModel.networkState.value, "Network state should be loaded")
    }

    @Test
    fun getUser_whenError(){

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