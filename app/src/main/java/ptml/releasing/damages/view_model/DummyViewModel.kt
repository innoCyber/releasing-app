package ptml.releasing.damages.view_model

import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

class DummyViewModel @Inject constructor(repository: Repository, appCoroutineDispatchers: AppCoroutineDispatchers) : BaseViewModel(repository, appCoroutineDispatchers) {

}