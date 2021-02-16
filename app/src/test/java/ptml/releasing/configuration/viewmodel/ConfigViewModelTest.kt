package ptml.releasing.configuration.viewmodel

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.base.BaseTest
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.data.*
import java.io.IOException


@Suppress("UNCHECKED_CAST")
class ConfigViewModelTest : BaseTest() {


    private val repository: ReleasingRepository = mockk()
    private val viewModel by lazy { ConfigViewModel(repository, dispatcher, updateChecker) }

    @Test
    fun `get admin configuration with registered IMEI when the device was not previously configured returns a success`() {
        coEvery {
            repository.getAdminConfigurationAsync(any())
        } returns getAdminConfigurationSuccess().toDeferredAsync() as Deferred<AdminConfigResponse>


        coEvery {
            repository.isConfiguredAsync()
        } returns isConfiguredFail()

        viewModel.getConfig(VALID_IMEI)


        assertThat(
            "the response should return a success",
            this.viewModel.configResponse.value,
            `is`(getAdminConfigurationSuccess())
        )

        assertThat(
            "Network state should be loaded",
            this.viewModel.networkState.value?.peekContent(), `is`(NetworkState.LOADED)
        )

    }


    @Test
    fun `get admin configuration  with invalid IMEI when the device was not previously configured returns a failure`() {
        coEvery {
            repository.getAdminConfigurationAsync(any())
        } returns getAdminConfigurationFail().toDeferredAsync() as Deferred<AdminConfigResponse>

        coEvery {
            repository.isConfiguredAsync()
        } returns isConfiguredFail()


        viewModel.getConfig(INVALID_IMEI)


        assertThat(
            "the response should return a failure",
            this.viewModel.configResponse.value, `is`(getAdminConfigurationFail())
        )

        assertThat(
            "Network state should be loaded",
            this.viewModel.networkState.value?.peekContent(), `is`(NetworkState.LOADED)
        )

    }

    @Test
    fun `get admin config when the device was previously configured returns a success`() {
        coEvery {
            repository.getAdminConfigurationAsync(any())
        } returns getAdminConfigurationSuccess().toDeferredAsync() as Deferred<AdminConfigResponse>

        coEvery {
            repository.isConfiguredAsync()
        } returns isConfiguredSuccess()


        coEvery {
            repository.getSelectedConfigAsync()
        } returns getSavedConfig()

        viewModel.getConfig("")

        assertThat(
            "the response should return a success",
            this.viewModel.configResponse.value,
            `is`(getAdminConfigurationSuccess())
        )

        assertThat(
            "The retrieved config data should be non-null",
            viewModel.configuration.value, `is`(getSavedConfig())
        )

        assertThat(
            "Network state should be loaded",
            this.viewModel.networkState.value?.peekContent(),
            `is`(NetworkState.LOADED)
        )
    }


    @Test
    fun `save admin config is successful`() {
        coEvery {
            repository.setSavedConfigAsync(any())
        } returns Unit

        coEvery {
            repository.setConfigured(any())
        } returns Unit

        coEvery {
            repository.setConfigurationDeviceAsync(
                mockCargoType().id,
                mockOperationStep().id,
                mockTerminal().id,
                provideImei()
            )
        } returns mockConfiguration().toDeferredAsync() as Deferred<ConfigureDeviceResponse>

        viewModel.setConfig(
            mockTerminal(),
            mockOperationStep(),
            mockCargoType(),
            true,
            provideImei()
        )

        assertThat(
            "The response should return a success (true)",
            viewModel.savedSuccess.value?.peekContent(), `is`(true)
        )

        assertThat(
            "State should be loaded",
            this.viewModel.networkState.value?.peekContent(),
            `is`(NetworkState.LOADED)
        )


    }


    @Test
    fun `save admin config fails with exception`() {
        coEvery {
            repository.setSavedConfigAsync(any())
        } throws IOException(SAVED_CONFIG_ERROR_MESSAGE)

        viewModel.setConfig(
            mockTerminal(),
            mockOperationStep(),
            mockCargoType(),
            true,
            provideImei()
        )

        assertThat(
            "An IOException should be thrown",
            this.viewModel.networkState.value?.peekContent()?.throwable?.message,
            `is`(NetworkState.error(IOException(SAVED_CONFIG_ERROR_MESSAGE)).throwable?.message)
        )


    }
}