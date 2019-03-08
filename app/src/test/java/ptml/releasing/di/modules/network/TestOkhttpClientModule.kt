package ptml.releasing.di.modules.network

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import ptml.releasing.di.scopes.ReleasingAppScope


@Module()
class TestOkhttpClientModule {


    @ReleasingAppScope
    @Provides
    fun provideOkhttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        return builder.build()
    }
}