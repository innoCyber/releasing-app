package ptml.releasing.app.di.modules.network

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ptml.releasing.app.data.remote.interceptor.ImeiInterceptor
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.errorlogger.InternetErrorLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

@Module(includes = [InterceptorsModule::class])
class OkhttpClientModule {

    @ReleasingAppScope
    @Provides
    fun provideFile(context: Context): File {
        return File(context.cacheDir, Constants.OK_HTTP_CACHE)
    }

    @ReleasingAppScope
    @Provides
    fun provideCache(file: File): Cache? {
        return Cache(file, 10 * 1000 * 1000)
    }

    @ReleasingAppScope
    @Provides
    fun provideOkhttpClient(
        cache: Cache?,
        httpInterceptor: HttpLoggingInterceptor,
        errorLoggingInterceptor: InternetErrorLoggingInterceptor,
        imeiInterceptor: ImeiInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(httpInterceptor)
        builder.addInterceptor(errorLoggingInterceptor)
        builder.addInterceptor(imeiInterceptor)
        builder.cache(cache)
        builder.retryOnConnectionFailure(true)
        builder.writeTimeout(Constants.WRITE_TIME_OUT, TimeUnit.SECONDS)
        builder.connectTimeout(Constants.CONNECT_TIME_OUT, TimeUnit.SECONDS)
        return builder.build()
    }
}