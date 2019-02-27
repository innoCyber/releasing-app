package it.ounet.releasing.di.modules.network


import it.ounet.releasing.BuildConfig
import it.ounet.releasing.di.scopes.ReleasingAppScope
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [OkhttpClientModule::class, NetworkFactoriesModule::class])
class RetrofitModule {

    @Provides
    @ReleasingAppScope
    fun provideRetrofit(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory, rxJavaCallAdapterFactory: RxJava2CallAdapterFactory): Retrofit{
        val builder = Retrofit.Builder()
        builder.client(client)
        builder.addCallAdapterFactory(rxJavaCallAdapterFactory)
        builder.addConverterFactory(gsonConverterFactory)
        builder.baseUrl(BuildConfig.BASE_URL)
        return builder.build()
    }




}