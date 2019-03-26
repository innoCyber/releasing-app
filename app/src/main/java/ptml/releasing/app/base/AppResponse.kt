package ptml.releasing.app.base

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
* All response classes in the application inherit from this class
* */
open class AppResponse{
    /**
     *Used in tests to wrap this as a @link Deferred
     * */
    fun toDeferredAsync() = GlobalScope.async { this@AppResponse }
}