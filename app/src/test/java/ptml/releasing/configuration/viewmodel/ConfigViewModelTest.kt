package ptml.releasing.configuration.viewmodel

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import org.junit.Test
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.base.BaseTest
import ptml.releasing.data.*
import java.io.IOException
import kotlin.test.assertEquals


class ConfigViewModelTest : BaseTest() {


    private val repository: ReleasingRepository = mockk()
    private val viewModel by lazy { ConfigViewModel(repository, dispatcher) }

    @Test
    fun `get admin configuration success`() {
        coEvery {
            repository.getAdminConfigurationAsync(any())
        } returns getAdminConfigurationSuccess().toDeferredAsync() as Deferred<AdminConfigResponse>


        coEvery{
            repository.isConfiguredAsync()
        }returns isConfiguredFail()

        viewModel.getConfig("")


        assertEquals(
            getAdminConfigurationSuccess(),
            this.viewModel.configResponse.value,
            "Verify the response returns a success"
        )

        assertEquals(
            NetworkState.LOADED,
            this.viewModel.networkState.value,
            "Network state should be loaded"
        )

    }


    @Test
    fun `get admin configuration fail`() {
        coEvery {
            repository.getAdminConfigurationAsync(any())
        } returns getAdminConfigurationFail().toDeferredAsync() as Deferred<AdminConfigResponse>

        coEvery{
            repository.isConfiguredAsync()
        }returns isConfiguredFail()


        viewModel.getConfig("")


        assertEquals(
            getAdminConfigurationFail(),
            this.viewModel.configResponse.value,
            "Verify the response returns a success"
        )

        assertEquals(
            NetworkState.LOADED,
            this.viewModel.networkState.value,
            "Network state should be loaded"
        )

    }

    @Test
    fun `get admin config with previously saved config`(){
        coEvery {
            repository.getAdminConfigurationAsync(any())
        } returns getAdminConfigurationSuccess().toDeferredAsync() as Deferred<AdminConfigResponse>


        coEvery{
            repository.isConfiguredAsync()
        }returns isConfiguredSuccess()


        coEvery{
            repository.getSavedConfigAsync()
        }returns getSavedConfig().toDeferredAsync() as Deferred<Configuration>

        viewModel.getConfig("")


        assertEquals(
            getAdminConfigurationSuccess(),
            this.viewModel.configResponse.value,
            "Verify the response returns a success"
        )

        assertEquals(getSavedConfig(), viewModel.configuration.value,
            "The retrieved config data")

        assertEquals(
            NetworkState.LOADED,
            this.viewModel.networkState.value,
            "Network state should be loaded"
        )

    }


    @Test
    fun `save admin config with success`(){
        coEvery {
            repository.setSavedConfigAsync(any())
        }returns Unit

        coEvery {
            repository.setConfigured(any())
        }returns Unit

        viewModel.setConfig(
            mockTerminal(),
            mockOperationStep(),
            mockCargoType(),
            true,
            (application as ReleasingApplication).provideImei()
        )

        assertEquals(true, viewModel.savedSuccess.value,
            "The config was saved")

        assertEquals(
            NetworkState.LOADED,
            this.viewModel.networkState.value,
            "State should be loaded"
        )


    }


    @Test
    fun `save admin config with error`(){
        coEvery {
            repository.setSavedConfigAsync(any())
        }throws  IOException(SAVED_CONFIG_ERROR_MESSAGE)

        viewModel.setConfig(
            mockTerminal(),
            mockOperationStep(),
            mockCargoType(),
            true,
            (application as ReleasingApplication).provideImei()
        )

        assertEquals(
            NetworkState.error(IOException(SAVED_CONFIG_ERROR_MESSAGE)).throwable?.message,
            this.viewModel.networkState.value?.throwable?.message,
            "Error occurred"
        )


    }
}