package ptml.releasing.ui.setup

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.squareup.okhttp.mockwebserver.MockResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ptml.releasing.api.DeviceConfigService
import ptml.releasing.base.BaseTest
import ptml.releasing.base.Message
import ptml.releasing.base.MessagesApi
import ptml.releasing.db.models.response.base.BaseResponse
import ptml.releasing.utils.NetworkState
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.HttpURLConnection
import retrofit2.converter.gson.GsonConverterFactory


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1], manifest = Config.NONE)
class SetupActivityViewModelTest:BaseTest(){



    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Force tests to be executed synchronously





    val disposable = CompositeDisposable()

    // FOR DATA
    private lateinit var activity: FragmentActivity
    private lateinit var viewModel: SetupActivityViewModel

    private val SAMPLE_IMEI = "0000"
    private val EXPECTED_RESPONSE = BaseResponse("", true)


    @Before
    override fun setUp(){
        super.setUp()
        this.activity = Robolectric.setupActivity(FragmentActivity::class.java)
        this.viewModel = ViewModelProviders.of(this.activity, viewModelFactory).get(SetupActivityViewModel::class.java)
    }

    // TESTS
    @Test
    fun getUser_whenSuccess() {
        // Prepare data
        this.mockHttpResponse("verifyDeviceIdSuccess.json", HttpURLConnection.HTTP_OK)
        // Pre-test
        kotlin.test.assertEquals(null, this.viewModel.baseLiveData.value, "Response should be null because stream not started yet")
        // Execute View Model
        this.viewModel.verifyDeviceId(SAMPLE_IMEI)
        // Checks
        kotlin.test.assertEquals(EXPECTED_RESPONSE, this.viewModel.baseLiveData.value, "Verify the response")
        kotlin.test.assertEquals(
            NetworkState.LOADED,
            this.viewModel.networkState.value,
            "Network state should be loaded"
        )
    }

    @Test
    fun getUser_whenError(){
        // Prepare data
        this.mockHttpResponse("verifyDeviceIdSuccess.json", HttpURLConnection.HTTP_BAD_GATEWAY)
        // Pre-test
        kotlin.test.assertEquals(null, this.viewModel.baseLiveData.value, "Response should be null because stream not started yet")
        // Execute View Model
        this.viewModel.verifyDeviceId(SAMPLE_IMEI)
        // Checks
        System.out.println("Url" + mockServer.requestCount)
        /*kotlin.test.assertEquals(null, this.viewModel.baseLiveData.value, "Response should be null")
        kotlin.test.assertEquals(
            NetworkState.error(""),
            this.viewModel.networkState.value,
            "The error should be thrown"
        )*/
    }


    @Test
    fun verifyMockApi(){
        var baseResponse:BaseResponse? = null
        var message: Message? = null
        var  throwable:Throwable? = null
        mockServer.enqueue(MockResponse().setBody("{\"text\":\"hello!\"}").setResponseCode(HttpURLConnection.HTTP_OK))

        var messageApi = /*Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(mockServer.url("/").toString())
            .client(OkHttpClient()).build()*/
        retrofit
            .create(MessagesApi::class.java)

        kotlin.test.assertNotNull(messageApi)

        val msgReg = messageApi.findMessage("")
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            message = it
        }, {
            throwable = it
        })

        kotlin.test.assertNotNull(message)



//        kotlin.test.assertNotNull(throwable)
    }


}