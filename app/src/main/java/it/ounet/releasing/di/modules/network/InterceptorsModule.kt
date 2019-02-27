package it.ounet.releasing.di.modules.network


import it.ounet.releasing.di.scopes.ReleasingAppScope
import dagger.Module
import dagger.Provides
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

@Module
class InterceptorsModule{

    @Provides
    @ReleasingAppScope
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            message -> Timber.e(message)
        }).setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}