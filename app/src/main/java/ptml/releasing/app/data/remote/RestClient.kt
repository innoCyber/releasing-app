package ptml.releasing.app.data.remote

import android.content.Context
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ptml.releasing.BuildConfig
import ptml.releasing.app.data.remote.interceptor.ConnectivityInterceptor
import ptml.releasing.app.data.remote.interceptor.DecryptionInterceptor
import ptml.releasing.app.data.remote.interceptor.EncryptionInterceptor
import ptml.releasing.app.data.remote.interceptor.ImeiInterceptor
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.encryption.DecryptionImpl
import ptml.releasing.app.utils.encryption.EncryptionImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by kryptkode on 10/23/2019.
 */

class RestClient @Inject constructor(
    context: Context,
    gson: Gson,
    @Named(Constants.DEBUG)
    debug: Boolean
) {

    companion object {
        private const val TIMEOUT: Long = 50
    }

    private val api: Api

    init {
        val loggingInterceptor = makeLoggingInterceptor(debug)
        val encryptloggingInterceptor = EncryptionInterceptor(EncryptionImpl())
        val decryptloggingInterceptor = DecryptionInterceptor(DecryptionImpl())

        val httpClient = OkHttpClient.Builder()
            .apply {
                connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                readTimeout(TIMEOUT, TimeUnit.SECONDS)

                addInterceptor(loggingInterceptor)
                addInterceptor(encryptloggingInterceptor)
                addInterceptor(decryptloggingInterceptor)
                addInterceptor(
                    ConnectivityInterceptor(
                        context
                    )
                )
                addInterceptor(ImeiInterceptor())
            }

        val client = httpClient.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
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
