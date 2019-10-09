package ptml.releasing.app.db.dao

import androidx.annotation.VisibleForTesting
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import ptml.releasing.app.db.model.InternetErrorLogModel
import ptml.releasing.app.db.model.InternetErrorLogModel.Companion.GET_LOGS_QUERY

@Dao
abstract class InternetErrorLogDao : BaseDao<InternetErrorLogModel>{

    @Query(GET_LOGS_QUERY)
    abstract fun getLogs(): DataSource.Factory<Int, InternetErrorLogModel>

    @Query(GET_LOGS_QUERY)
    @VisibleForTesting
    abstract fun getLogsList(): List<InternetErrorLogModel>
}