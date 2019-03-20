package ptml.releasing.device_configuration.viewmodel

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.base.BaseTest
import ptml.releasing.data.getVerifyDeviceException
import ptml.releasing.data.getVerifyDeviceFail
import ptml.releasing.data.getVerifyDeviceSuccess
import kotlin.test.assertEquals

class DeviceConfigViewModelTest : BaseTest() {


    private val repository: ReleasingRepository = mockk()

    private val deviceConfigViewModel by lazy { DeviceConfigViewModel(repository, dispatcher) }


    @ExperimentalCoroutinesApi
    @Test
    fun `verify device with a valid IMEI`() {

        coEvery {
            repository.verifyDeviceId(any())
        } returns getVerifyDeviceSuccess().toDefferred()

        deviceConfigViewModel.verifyDeviceId("")

        assertEquals(
            getVerifyDeviceSuccess(),
            this.deviceConfigViewModel.baseLiveData.value,
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
            repository.verifyDeviceId(any())
        } returns getVerifyDeviceFail().toDefferred()


        deviceConfigViewModel.verifyDeviceId("222")


        assertEquals(
            getVerifyDeviceFail(),
            deviceConfigViewModel.baseLiveData.value,
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
            repository.verifyDeviceId(any())
        } throws getVerifyDeviceException()

        deviceConfigViewModel.verifyDeviceId("")

        assertEquals(
            null,
            deviceConfigViewModel.baseLiveData.value,
            "The response is null"
        )

        assertEquals(
            NetworkState.error(getVerifyDeviceException()).throwable?.message,
            deviceConfigViewModel.networkState.value?.throwable?.message,
            "The error is caught"
        )

    }
}


