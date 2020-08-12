package ptml.releasing.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import ptml.releasing.app.data.remote.exception.NoConnectivityException
import retrofit2.HttpException
import java.net.ConnectException

/**
 * Created by kryptkode on 10/23/2019.
 */

class NetworkUtil {

    companion object {

        suspend fun exceptionHandler(
            passedFunction: suspend () -> Unit,
            handler: ((code: Int?, message: String?) -> Unit)?
        ) {
            try {
                passedFunction()
            } catch (e: HttpException) {
                handler?.let { it(e.code(), e.message()) }
            } catch (e: NoConnectivityException) {
                handler?.let { it(null, e.message) }
            } catch (e: ConnectException) {
                handler?.let { it(null, e.message) }
            } catch (e: Exception) {
                handler?.let { it(null, e.message) }
            }
        }

        @Suppress("DEPRECATION")
        @SuppressLint("MissingPermission")
        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
    }
}
