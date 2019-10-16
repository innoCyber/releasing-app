package ptml.releasing.internet_error_logs.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

interface ErrorCache {
    fun getErrors(): LiveData<PagedList<ErrorLog>>
    suspend fun logError(log: ErrorLog)
    suspend fun getAllLogs(): List<ErrorLog>
}