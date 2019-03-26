package ptml.releasing.damages.viewmodel

import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import javax.inject.Inject

class DamageViewModel @Inject constructor(
        var repository: Repository,
        var appCoroutineDispatchers: AppCoroutineDispatchers) : BaseViewModel() {


    fun downloadDamages() {

    }

}