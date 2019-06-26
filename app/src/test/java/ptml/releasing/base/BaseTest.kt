package ptml.releasing.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.junit.Before
import org.junit.Rule
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker

abstract class BaseTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule() //ensures that the test is executed synchronously


    protected val updateChecker: RemoteConfigUpdateChecker = mockk()
    protected val dispatcher: AppCoroutineDispatchers = mockk()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        clearAllMocks()
        every {
            dispatcher.network
        } returns Dispatchers.Unconfined

        every {
            dispatcher.db
        } returns Dispatchers.Unconfined
        every {
            dispatcher.main
        } returns Dispatchers.Unconfined
    }

}
