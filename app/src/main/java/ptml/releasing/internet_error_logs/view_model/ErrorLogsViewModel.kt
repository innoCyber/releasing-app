package ptml.releasing.internet_error_logs.view_model

import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.internet_error_logs.model.ErrorCache
import javax.inject.Inject

class ErrorLogsViewModel @Inject constructor(
    private val errorCache: ErrorCache,
    updateChecker: RemoteConfigUpdateChecker,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    val errorLogs =  errorCache.getErrors()
}