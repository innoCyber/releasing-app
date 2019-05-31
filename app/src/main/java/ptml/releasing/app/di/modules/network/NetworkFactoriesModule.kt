package ptml.releasing.app.di.modules.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import dagger.Module
import dagger.Provides
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.app.utils.CoroutineCallAdapterFactory
import ptml.releasing.app.utils.NullOnEmptyConverterFactory
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
    fun provideNullOrEmptyConverterFactory(): NullOnEmptyConverterFactory {
        return NullOnEmptyConverterFactory()
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