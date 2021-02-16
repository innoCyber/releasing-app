package ptml.releasing.form.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.clearAllMocks
import org.junit.Before
import org.junit.Rule


abstract class BaseTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule() //ensures that the test is executed synchronously


    @Before
    fun setup() {
        clearAllMocks()
    }

}
