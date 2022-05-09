package ptml.releasing.app.utils.encryption

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.common.util.Hex
import okhttp3.RequestBody
import okio.Buffer
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESEncryption {

    private var secretKeyValue = "4F9E1E2M5G2U4I1K"
    private val initializationVectorValue = "9Z8E4X1A3H5T75U7"
    private val CIPHER_ALGORITHM = "AES"
    private val CIPHER_TRANSFORMATION_TYPE = "AES/CBC/PKCS5PADDING"


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        InvalidKeyException::class,
        UnsupportedEncodingException::class,
        InvalidAlgorithmParameterException::class
    )
    fun encrypt(message: String): String? {
        val stringCharSet: ByteArray = message.toByteArray(StandardCharsets.UTF_8)
        val secretKeySpec = SecretKeySpec(secretKeyValue.toByteArray(), CIPHER_ALGORITHM)
        val initializationVectorParamSpec = IvParameterSpec(initializationVectorValue.toByteArray())
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_TYPE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, initializationVectorParamSpec)
        val dstBuff = cipher.doFinal(stringCharSet)
        return Hex.bytesToStringLowercase(dstBuff)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        UnsupportedEncodingException::class
    )
    fun decrypt(encrypted: String?): String {
        val secretKeySpec = SecretKeySpec(secretKeyValue.toByteArray(), CIPHER_ALGORITHM)
        val initializationVectorParamSpec = IvParameterSpec(initializationVectorValue.toByteArray())
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_TYPE)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, initializationVectorParamSpec)
        val raw = Hex.stringToBytes(encrypted)
        val originalBytes = cipher.doFinal(raw)
        return String(originalBytes, StandardCharsets.UTF_8)
    }


    @Throws(IOException::class)
    fun requestBodyToString(requestBody: RequestBody?): String {
        val buffer = Buffer()
        requestBody?.writeTo(buffer)
        return buffer.readUtf8()
    }

}