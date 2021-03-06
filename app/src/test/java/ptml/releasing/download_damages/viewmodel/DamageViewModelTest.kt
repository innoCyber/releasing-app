package ptml.releasing.download_damages.viewmodel

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Test
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.base.BaseTest
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.data.DAMAGE_SIZE
import ptml.releasing.data.VALID_IMEI
import ptml.releasing.data.downloadDamagesSuccess

@Suppress("UNCHECKED_CAST")
class DamageViewModelTest : BaseTest() {
    private val repository: ReleasingRepository = mockk()
    private val viewModel by lazy { DamageViewModel(repository, dispatcher, updateChecker) }


    @ExperimentalCoroutinesApi
    @Test
    fun `get damages with valid IMEI is successful`() {
        coEvery {
            repository.getDamagesAsync(any())
        }returns downloadDamagesSuccess().toDeferredAsync() as Deferred<DamageResponse>

        viewModel.getDamages(VALID_IMEI)

        assertEquals(DAMAGE_SIZE, viewModel.response.value?.size)

    }



}