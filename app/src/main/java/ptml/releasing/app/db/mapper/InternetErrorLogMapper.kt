package ptml.releasing.app.db.mapper

import ptml.releasing.app.db.model.InternetErrorLogModel
import ptml.releasing.internet_error_logs.model.ErrorLog
import javax.inject.Inject

class InternetErrorLogMapper @Inject constructor() : DbMapper<InternetErrorLogModel, ErrorLog>{
    override fun mapFromCached(type: InternetErrorLogModel): ErrorLog {
        return ErrorLog(type.id, type.description, type.url, type.error, type.date)
    }

    override fun mapToCached(type: ErrorLog): InternetErrorLogModel {
        return InternetErrorLogModel(type.id, type.description, type.url, type.error, type.date)
    }

}