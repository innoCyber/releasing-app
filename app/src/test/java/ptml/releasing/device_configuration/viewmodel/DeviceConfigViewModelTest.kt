package ptml.releasing.device_configuration.viewmodel

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.base.BaseTest
import ptml.releasing.data.VALID_IMEI
import ptml.releasing.data.getVerifyDeviceException
import ptml.releasing.data.getVerifyDeviceFail
import ptml.releasing.data.getVerifyDeviceSuccess
import kotlin.test.assertEquals

@Suppress("UNCHECKED_CAST")
class DeviceConfigViewModelTest : BaseTest() {


    private val repository: ReleasingRepository = mockk()
    private val deviceConfigViewModel by lazy { DeviceConfigViewModel(repository, dispatcher, updateChecker) }


    @ExperimentalCoroutinesApi
    @Test
    fun `verify device with a valid IMEI`() {

        coEvery {
            repository.verifyDeviceIdAsync(any())
        } returns getVerifyDeviceSuccess().toDeferredAsync() as Deferred<BaseResponse>

        every {
            repository.setFirst(any())
        }returns Unit

        every {
            repository.setImei(any())
        }returns Unit


        deviceConfigViewModel.verifyDeviceId(VALID_IMEI)

        assertEquals(
            Unit,
            this.deviceConfigViewModel.openSearchActivity.value,
            "Verify the response returns a success"
        )

        assertEquals(
            NetworkState.LOADED,
            this.deviceConfigViewModel.networkState.value,
            "Network state should be loaded"
        )
    }


    @ExperimentalCoroutinesApi
    @Test
    fun `verify device with an invalid IMEI`() {
        coEvery {
            repository.verifyDeviceIdAsync(any())
        } returns getVerifyDeviceFail().toDeferredAsync() as Deferred<BaseResponse>

        every {
            repository.setFirst(any())
        }returns Unit

        deviceConfigViewModel.verifyDeviceId("222")


        assertEquals(
            Unit,
            deviceConfigViewModel.showDeviceError.value,
            "The response returns a failure"
        )

        assertEquals(
            NetworkState.LOADED,
            this.deviceConfigViewModel.networkState.value,
            "Network state should be loaded"
        )

    }


    @ExperimentalCoroutinesApi
    @Test
    fun `verify device fails with network error`() {
        coEvery {
            repository.verifyDeviceIdAsync(any())
        } throws getVerifyDeviceException()

        deviceConfigViewModel.verifyDeviceId("")

        assertEquals(
            null,
            deviceConfigViewModel.openSearchActivity.value,
            "The response is null"
        )

        assertEquals(
            null,
            deviceConfigViewModel.showDeviceError.value,
            "The response is null"
        )

        assertEquals(
            NetworkState.error(getVerifyDeviceException()).throwable?.message,
            deviceConfigViewModel.networkState.value?.throwable?.message,
            "The error is caught"
        )

    }
}


