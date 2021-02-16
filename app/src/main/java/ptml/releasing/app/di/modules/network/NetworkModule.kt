package ptml.releasing.app.di.modules.network


import android.text.TextUtils
import ptml.releasing.app.utils.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import ptml.releasing.BuildConfig
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.app.utils.NullOnEmptyConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Named

const val BASE_URL = "base_url"

@Module(includes = [OkhttpClientModule::class, NetworkFactoriesModule::class])
class NetworkModule {

    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
        nullOnEmptyConverterFactory: NullOnEmptyConverterFactory,
        coroutineCallAdapterFactory: CoroutineCallAdapterFactory, @Named(
            BASE_URL
        ) baseUrl: String
    ): Retrofit {
        val builder = Retrofit.Builder()
        Timber.e("OKHTTTP: ${client.interceptors()}")
        builder.client(client)
        builder.addCallAdapterFactory(coroutineCallAdapterFactory)
        builder.addConverterFactory(nullOnEmptyConverterFactory)
        builder.addConverterFactory(gsonConverterFactory)
        builder.baseUrl(baseUrl)
        return builder.build()
    }

    @Provides
    @Named(BASE_URL)
    fun provideBaseUrl(prefs: Prefs): String {
        val serverUrl = prefs.getServerUrl()
        Timber.d("Server URL: %s", serverUrl)
        return if (TextUtils.isEmpty(serverUrl)) BuildConfig.BASE_URL else serverUrl!!
    }


}