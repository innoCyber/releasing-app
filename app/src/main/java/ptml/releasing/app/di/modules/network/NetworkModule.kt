package ptml.releasing.app.di.modules.network



import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import ptml.releasing.BuildConfig
import ptml.releasing.app.di.scopes.ReleasingAppScope
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

const val BASE_URL ="base_url"
@Module(includes = [OkhttpClientModule::class, NetworkFactoriesModule::class])
class NetworkModule {

    @Provides
    @ReleasingAppScope
    fun provideRetrofit(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory, coroutineCallAdapterFactory: CoroutineCallAdapterFactory, @Named(
        BASE_URL) baseUrl:String): Retrofit{
        val builder = Retrofit.Builder()
        builder.client(client)
        builder.addCallAdapterFactory(coroutineCallAdapterFactory)
        builder.addConverterFactory(gsonConverterFactory)
        builder.baseUrl(baseUrl)
        return builder.build()
    }

    @Provides
    @Named(BASE_URL)
    fun  provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }



}