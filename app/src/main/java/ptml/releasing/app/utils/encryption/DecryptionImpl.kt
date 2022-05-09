package ptml.releasing.app.utils.encryption

import android.os.Build
import androidx.annotation.RequiresApi
import javax.inject.Inject

class DecryptionImpl @Inject constructor() : CryptoStrategy {
    override fun encrypt(body: String?): String {
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @Throws(Exception::class)
    override fun decrypt(data: String?): String {
        return AESEncryption().decrypt(data)
    }
}