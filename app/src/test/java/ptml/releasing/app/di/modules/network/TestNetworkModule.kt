package ptml.releasing.app.di.modules.network




import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer

import ptml.releasing.app.di.scopes.ReleasingAppScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

const val TEST_URL="tst"
@Module(includes = [TestOkhttpClientModule::class, NetworkFactoriesModule::class])
class TestNetworkModule {


    @Provides
    @ReleasingAppScope
    fun provideRetrofit(client: OkHttpClient, gsonConverterFactory: GsonConverterFactory, coroutineCallAdapterFactory: CoroutineCallAdapterFactory, @Named(
        TEST_URL) baseUrl:String): Retrofit{
        val builder = Retrofit.Builder()
        builder.client(client)
        builder.addCallAdapterFactory(coroutineCallAdapterFactory)
        builder.addConverterFactory(gsonConverterFactory)
        builder.baseUrl(baseUrl)
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