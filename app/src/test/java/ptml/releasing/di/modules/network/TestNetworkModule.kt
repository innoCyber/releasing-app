package ptml.releasing.di.modules.network


import com.squareup.okhttp.mockwebserver.MockWebServer
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

import ptml.releasing.di.scopes.ReleasingAppScope
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

const val TEST_URL="tst"
@Module(includes = [TestOkhttpClientModule::class, NetworkFactoriesModule::class])
class TestNetworkModule {


    @Provides
    @ReleasingAppScope
    fun provideRetrofit(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory, rxJavaCallAdapterFactory: RxJava2CallAdapterFactory, @Named(
        TEST_URL) baseUrl:String): Retrofit{
        val builder = Retrofit.Builder()
        builder.client(client)
        builder.addCallAdapterFactory(rxJavaCallAdapterFactory)
        builder.addConverterFactory(gsonConverterFactory)
        builder.baseUrl(baseUrl)
        System.out.println("TestNetworkModule " + baseUrl)
        return builder.build()
    }


    @Provides
    @ReleasingAppScope
    @Named(TEST_URL)
    fun  provideBaseUrl(server: MockWebServer): String {
        return server.url("/").toString()
    }

    @Provides
    @ReleasingAppScope
    fun provideMockSever():MockWebServer{
        return MockWebServer()
    }


}