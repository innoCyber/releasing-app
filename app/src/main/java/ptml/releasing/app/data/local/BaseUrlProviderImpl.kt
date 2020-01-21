package ptml.releasing.app.data.local

import ptml.releasing.app.data.remote.BaseUrlProvider
import javax.inject.Inject

/**
 * Created by kryptkode on 11/19/2019.
 */
class BaseUrlProviderImpl @Inject constructor(
    private val localDataManager: LocalDataManager
) : BaseUrlProvider {

    override fun getUrl(): String {
        return localDataManager.getServerBaseUrl()
    }
}