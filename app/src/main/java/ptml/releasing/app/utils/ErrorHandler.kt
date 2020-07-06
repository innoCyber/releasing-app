package ptml.releasing.app.utils

import android.content.Context
import ptml.releasing.R
import ptml.releasing.app.exception.AppException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorHandler(private val context: Context) {

    fun getErrorMessage(e: Throwable?): String {
        if (e is ConnectException) {
            return getString(R.string.connect_exception)
        }
        if (e is UnknownHostException) {
            return getString(R.string.unknown_host_exception)
        }
        if (e is SocketTimeoutException) {
            return getString(R.string.timed_out_exception)
        }
        if (e is AppException) {
            return e.message ?: getString(R.string.unknown_exception)
        }
        return getString(R.string.unknown_exception)
    }

    private fun getString(resId: Int): String {
        return context.getString(resId)
    }
}