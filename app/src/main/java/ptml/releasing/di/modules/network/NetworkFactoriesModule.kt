package ptml.releasing.di.modules.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ptml.releasing.di.scopes.ReleasingAppScope
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
    fun provideRxJavaAdapterFactory(): RxJava2CallAdapterFactory{
        return RxJava2CallAdapterFactory.create()
    }

    @Provides
    @ReleasingAppScope
    fun provideGson(): Gson{
        val builder = GsonBuilder()
        return builder.create()
    }
}