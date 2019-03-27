package ptml.releasing.damages.viewmodel

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Test
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.base.BaseTest
import ptml.releasing.damages.model.DamageResponse
import ptml.releasing.data.DAMAGE_SIZE
import ptml.releasing.data.IMEI
import ptml.releasing.data.downloadDamagesSuccess

class DamageViewModelTest : BaseTest() {
    private val repository: ReleasingRepository = mockk()
    private val viewModel by lazy { DamageViewModel(repository, dispatcher) }


    @ExperimentalCoroutinesApi
    @Test
    fun `get damages with valid IMEI`() {
        coEvery {
            repository.getDamagesAsync(any())
        }returns downloadDamagesSuccess().toDeferredAsync() as Deferred<DamageResponse>

        viewModel.getDamages(IMEI)

        assertEquals(DAMAGE_SIZE, viewModel.response.value?.size)

    }



}