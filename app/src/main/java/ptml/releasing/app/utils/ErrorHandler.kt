package ptml.releasing.app.utils

import android.content.Context
import ptml.releasing.R
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class ErrorHandler {


    fun getErrorMessage(e: Throwable?): Int {
        if (e is ConnectException) {
            return R.string.connect_exception
        }
        if (e is UnknownHostException) {
            return R.string.unknown_host_exception
        }
        if (e is SocketTimeoutException) {
            return R.string.timed_out_exception
        }
        return R.string.unknown_exception
    }
}