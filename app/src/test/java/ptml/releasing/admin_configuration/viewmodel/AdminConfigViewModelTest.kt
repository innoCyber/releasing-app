package ptml.releasing.admin_configuration.viewmodel

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import org.junit.Test
import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.base.BaseTest
import ptml.releasing.data.getAdminConfigurationFail
import ptml.releasing.data.getAdminConfigurationSuccess
import kotlin.test.assertEquals


class AdminConfigViewModelTest : BaseTest() {


    private val repository: ReleasingRepository = mockk()
    private val viewModel by lazy { AdminConfigViewModel(repository, dispatcher) }

    @Test
    fun `get admin configuration success`() {
        coEvery {
            repository.getAdminConfiguration(any())
        } returns getAdminConfigurationSuccess().toDefferred() as Deferred<AdminConfigResponse>

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
            repository.getAdminConfiguration(any())
        } returns getAdminConfigurationFail().toDefferred() as Deferred<AdminConfigResponse>

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
}