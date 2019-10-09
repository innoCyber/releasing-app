package ptml.releasing.app.utils.errorlogger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.internet_error_logs.model.ErrorCache
import ptml.releasing.internet_error_logs.model.ErrorLog
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class Logger @Inject constructor(private val errorCache: ErrorCache,
                                 private val appCoroutineDispatchers: AppCoroutineDispatchers) : CoroutineScope{
    override val coroutineContext: CoroutineContext
        get() = appCoroutineDispatchers.db

    fun logError(description:String, url:String, error:String){
        val log = ErrorLog(description, url,error,  Date())
        launch {
            errorCache.logError(log)
        }
    }
}