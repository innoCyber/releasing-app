package ptml.releasing.cargo_info.view_model

import io.mockk.mockk
import org.junit.Test
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.base.BaseTest
import ptml.releasing.cargo_info.view.CargoInfoActivity

class CargoInfoViewModelTest : BaseTest() {

    private val repository: ReleasingRepository = mockk()

    private val viewModel by lazy { CargoInfoViewModel(repository, dispatcher, updateChecker) }

    private val activity by lazy {
        CargoInfoActivity
    }

    @Test
    fun `test form body for uploadData`(){

    }


}