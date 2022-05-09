package ptml.releasing.app.data.remote.interceptor

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import ptml.releasing.app.utils.encryption.CryptoStrategy
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class DecryptionInterceptor @Inject constructor(private val mDecryptionStrategy: CryptoStrategy?) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful) {
            val newResponse = response.newBuilder()
            var contentType = response.header("Content-Type")
            if (TextUtils.isEmpty(contentType)) contentType = "application/json"
            //            InputStream cryptedStream = response.body().byteStream();
            val responseStr = response.body()!!.string()
            var decryptedString: String? = null
            if (mDecryptionStrategy != null) {
                try {
                    decryptedString = mDecryptionStrategy.decrypt(responseStr)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Timber.i("Response string => %s", responseStr)
                Timber.i("Decrypted BODY=> %s", decryptedString)
            } else {
                throw IllegalArgumentException("No decryption strategy!")
            }
            newResponse.body(ResponseBody.create(MediaType.parse(contentType), decryptedString))
            return newResponse.build()
        }
        return response
    }

}
