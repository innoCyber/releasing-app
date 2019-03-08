package ptml.releasing.di.modules.network


import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import ptml.releasing.BuildConfig
import ptml.releasing.di.scopes.ReleasingAppScope
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

const val BASE_URL ="base_url"
@Module(includes = [OkhttpClientModule::class, NetworkFactoriesModule::class])
class NetworkModule {

    @Provides
    @ReleasingAppScope
    fun provideRetrofit(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory, rxJavaCallAdapterFactory: RxJava2CallAdapterFactory, @Named(
        BASE_URL) baseUrl:String): Retrofit{
        val builder = Retrofit.Builder()
        builder.client(client)
        builder.addCallAdapterFactory(rxJavaCallAdapterFactory)
        builder.addConverterFactory(gsonConverterFactory)
        builder.baseUrl(baseUrl)
        System.out.println("NetworkModule" + baseUrl)
        return builder.build()
    }

    @Provides
    @Named(BASE_URL)
    fun  provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }



}