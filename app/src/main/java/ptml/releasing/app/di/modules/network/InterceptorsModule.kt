package ptml.releasing.app.di.modules.network


import dagger.Module
import dagger.Provides
import okhttp3.logging.HttpLoggingInterceptor
import ptml.releasing.BuildConfig
import ptml.releasing.app.di.scopes.ReleasingAppScope
import timber.log.Timber

@Module
class InterceptorsModule{

    @Provides
    @ReleasingAppScope
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            message -> Timber.e(message)
        }).setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
    }
}