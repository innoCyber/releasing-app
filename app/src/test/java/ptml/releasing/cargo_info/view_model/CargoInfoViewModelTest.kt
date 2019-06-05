package ptml.releasing.cargo_info.view_model

import io.mockk.mockk
import org.junit.Test
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.base.BaseTest

class CargoInfoViewModelTest : BaseTest() {

    private val repository: ReleasingRepository = mockk()
    private val viewModel by lazy { CargoInfoViewModel(repository, dispatcher) }

    @Test
    fun `get`(){

    }


}