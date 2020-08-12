package ptml.releasing.app.data.remote.interceptor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import ptml.releasing.app.data.remote.exception.NoConnectivityException
import ptml.releasing.app.utils.NetworkUtil


/**
 * Created by kryptkode on 10/23/2019.
 */

class ConnectivityInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkUtil.isOnline(context)) {
            throw NoConnectivityException(
                "Not connected to internet."
            )
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}
