package ptml.releasing.app.data.remote

import android.content.Context
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ptml.releasing.BuildConfig
import ptml.releasing.app.data.remote.interceptor.ConnectivityInterceptor
import ptml.releasing.app.data.remote.interceptor.ImeiInterceptor
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.app.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by kryptkode on 10/23/2019.
 */

class VoyageRestClient @Inject constructor(
    context: Context,
    gson: Gson,
    @Named(Constants.DEBUG)
    debug: Boolean,
    var prefs: Prefs
) {

    companion object {
        private const val TIMEOUT: Long = 50
    }

    private val api: Api

    init {

        val loggingInterceptor = makeLoggingInterceptor(debug)

        val httpClient = OkHttpClient.Builder()
            .apply {
                connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                readTimeout(TIMEOUT, TimeUnit.SECONDS)

                addInterceptor(loggingInterceptor)
                addInterceptor(
                    ConnectivityInterceptor(
                        context
                    )
                )
                addInterceptor(ImeiInterceptor())
            }

        val serverUrl = prefs.getServerUrl()
        val client = httpClient.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(if(serverUrl.isNullOrEmpty()) BuildConfig.BASE_VOYAGE_URL else serverUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        api = retrofit.create(Api::class.java)
    }

    private fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (isDebug) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    fun getRemoteCaller() = api
}
