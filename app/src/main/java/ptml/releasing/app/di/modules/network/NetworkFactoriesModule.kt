package ptml.releasing.app.di.modules.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import ptml.releasing.app.di.scopes.ReleasingAppScope
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkFactoriesModule {

    @Provides
    @ReleasingAppScope
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @ReleasingAppScope
    fun provideCoroutineAdapterFactory(): CoroutineCallAdapterFactory {
        return CoroutineCallAdapterFactory()
    }


    @Provides
    @ReleasingAppScope
    fun provideGson(): Gson{
        val builder = GsonBuilder()
        return builder.create()
    }
}