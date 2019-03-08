package ptml.releasing.base


import androidx.lifecycle.ViewModelProvider
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import ptml.releasing.di.component.DaggerTestAppComponent
import ptml.releasing.di.component.TestAppComponent
import java.io.File
import javax.inject.Inject

abstract class BaseApiTest {

    lateinit var testAppComponent: TestAppComponent

    @Inject
    lateinit var mockServer: MockWebServer

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Before
    open fun setUp() {
        this.configureDi()
        this.configureMockServer()
    }

    @After
    open fun tearDown() {
        this.stopMockServer()
    }


    open fun configureDi() {
        this.testAppComponent = DaggerTestAppComponent.builder()
            .build()
        this.testAppComponent.inject(this)
    }


    open fun configureMockServer() {
        mockServer.start()
    }

    open fun stopMockServer() {
        mockServer.shutdown()
    }

    open fun mockHttpResponse(fileName: String, responseCode: Int) {
        val body = getJson(fileName)
        mockServer.enqueue(MockResponse()
            .setResponseCode(responseCode)
                .setBody(body))
    }

    private fun getJson(path: String): String {
        val uri = this.javaClass.classLoader.getResource(path)
        val file = File(uri.path)
        return String(file.readBytes())
    }
}
