package ptml.releasing.app.db

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ptml.releasing.app.db.mapper.InternetErrorLogMapper
import ptml.releasing.internet_error_logs.model.ErrorCache
import ptml.releasing.internet_error_logs.model.ErrorLog
import javax.inject.Inject

class ErrorCacheImpl @Inject constructor(
    private val mapper: InternetErrorLogMapper,
    releasingDb: ReleasingDb
) : ErrorCache {
    private val internetErrorLogDao = releasingDb.internetErrorDao()

    override fun getErrors(): LiveData<PagedList<ErrorLog>> {

        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(INITIAL_PAGE_SIZE)
            .setPageSize(PAGE_SIZE)
            .build()

        val dataSource = internetErrorLogDao.getLogs().map {
            mapper.mapFromCached(it)
        }

        val data = LivePagedListBuilder(
            dataSource,
            pagedListConfig
        ).build()

        return data
    }

    override suspend fun logError(log: ErrorLog) {
        internetErrorLogDao.insert(mapper.mapToCached(log))
    }


    companion object {
        private const val PAGE_SIZE = 10
        private const val INITIAL_PAGE_SIZE = 20
    }

}