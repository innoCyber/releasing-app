package ptml.releasing.app.data.remote.interceptor

import okhttp3.*
import ptml.releasing.app.utils.encryption.AESEncryption
import ptml.releasing.app.utils.encryption.CryptoStrategy
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class EncryptionInterceptor @Inject constructor(private val mEncryptionStrategy: CryptoStrategy?) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        Timber.i("===============ENCRYPTING REQUEST===============")
        var request: Request? = chain.request()
        val rawBody: RequestBody? = request?.body()

        Timber.i("Raw body1=> %s", rawBody)
        var encryptedBody = ""
        val mediaType = MediaType.parse("text/plain; charset=utf-8")
        if (mEncryptionStrategy != null) {
            try {
                val rawBodyString: String = AESEncryption().requestBodyToString(rawBody)
                encryptedBody = mEncryptionStrategy.encrypt(rawBodyString)
                Timber.i("Raw body=> %s", rawBodyString)
                Timber.i("Encrypted BODY=> %s", encryptedBody)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            throw IllegalArgumentException("No encryption strategy!")
        }
        val body = RequestBody.create(mediaType, encryptedBody)

        //build new request
        request = request?.newBuilder()
            ?.header("Content-Type", body.contentType().toString())
            ?.header("Content-Length", body.contentLength().toString())
            ?.method(request.method(), body)?.build()
        return chain.proceed(request)
    }

    companion object {
        private val TAG = EncryptionInterceptor::class.java.simpleName
    }

}