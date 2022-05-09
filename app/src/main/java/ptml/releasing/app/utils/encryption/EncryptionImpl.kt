package ptml.releasing.app.utils.encryption

import android.os.Build
import androidx.annotation.RequiresApi
import javax.inject.Inject

class EncryptionImpl @Inject constructor() : CryptoStrategy {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @Throws(Exception::class)
    override fun encrypt(body: String?): String {
        return body?.let { AESEncryption().encrypt(it) }!!
    }

    override fun decrypt(data: String?): String? {
        return null
    }
}