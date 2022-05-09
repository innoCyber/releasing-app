package ptml.releasing.app.di.modules.network

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ptml.releasing.app.data.remote.interceptor.DecryptionInterceptor
import ptml.releasing.app.data.remote.interceptor.EncryptionInterceptor
import ptml.releasing.app.data.remote.interceptor.ImeiInterceptor
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.encryption.CryptoStrategy
import ptml.releasing.app.utils.encryption.DecryptionImpl
import ptml.releasing.app.utils.encryption.EncryptionImpl
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
    fun provideCryptoStrategy(): CryptoStrategy {
        return EncryptionImpl()
    }

    @ReleasingAppScope
    @Provides
    fun provideEncryptionImpl(): EncryptionImpl {
        return EncryptionImpl()
    }

    @ReleasingAppScope
    @Provides
    fun provideDecryptionImpl(): DecryptionImpl {
        return DecryptionImpl()
    }

    @ReleasingAppScope
    @Provides
    fun provideEncryptionInterceptor(mEncryptionStrategy: CryptoStrategy): EncryptionInterceptor {
        return EncryptionInterceptor(mEncryptionStrategy)
    }

    @ReleasingAppScope
    @Provides
    fun provideDecryptionInterceptor(mEncryptionStrategy: CryptoStrategy): DecryptionInterceptor {
        return DecryptionInterceptor(mEncryptionStrategy)
    }

    @ReleasingAppScope
    @Provides
    fun provideOkhttpClient(
        cache: Cache?,
        httpInterceptor: HttpLoggingInterceptor,
        errorLoggingInterceptor: InternetErrorLoggingInterceptor,
        imeiInterceptor: ImeiInterceptor,
        encryptionInterceptor: EncryptionInterceptor,
        decryptionInterceptor: DecryptionInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(httpInterceptor)
        builder.addInterceptor(encryptionInterceptor)
        builder.addInterceptor(decryptionInterceptor)
        builder.addInterceptor(errorLoggingInterceptor)
        builder.addInterceptor(imeiInterceptor)
        builder.cache(cache)
        builder.retryOnConnectionFailure(true)
        builder.writeTimeout(Constants.WRITE_TIME_OUT, TimeUnit.SECONDS)
        builder.connectTimeout(Constants.CONNECT_TIME_OUT, TimeUnit.SECONDS)
        builder.readTimeout(Constants.READ_TIME_OUT, TimeUnit.SECONDS)
        return builder.build()
    }
}