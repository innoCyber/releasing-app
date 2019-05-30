package ptml.releasing.app.di.modules.network


import android.text.TextUtils
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import ptml.releasing.BuildConfig
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.app.prefs.Prefs
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Named

const val BASE_URL = "base_url"

@Module(includes = [OkhttpClientModule::class, NetworkFactoriesModule::class])
class NetworkModule {

    @Provides
    @ReleasingAppScope
    fun provideRetrofit(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory, coroutineCallAdapterFactory: CoroutineCallAdapterFactory, @Named(
            BASE_URL) baseUrl: String): Retrofit {
        val builder = Retrofit.Builder()
        builder.client(client)
        builder.addCallAdapterFactory(coroutineCallAdapterFactory)
        builder.addConverterFactory(gsonConverterFactory)
        builder.baseUrl(baseUrl)
        return builder.build()
    }

    @Provides
    @Named(BASE_URL)
    fun provideBaseUrl(prefs: Prefs): String {
        val serverUrl = prefs.getServerUrl()
        Timber.d("Server URL: %s", serverUrl )
        return if (TextUtils.isEmpty(serverUrl)) BuildConfig.BASE_URL else serverUrl!!
    }


}