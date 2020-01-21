package ptml.releasing.app.data.remote

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import ptml.releasing.app.utils.NetworkUtil
import ptml.releasing.app.utils.NoConnectivityException


/**
 * Created by kryptkode on 10/23/2019.
 */

class ConnectivityInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkUtil.isOnline(context)) {
            throw NoConnectivityException("Not connected to internet.")
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}
