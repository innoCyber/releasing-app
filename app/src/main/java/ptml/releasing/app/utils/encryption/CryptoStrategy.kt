package ptml.releasing.app.utils.encryption


interface CryptoStrategy {
    @Throws(Exception::class)
    fun encrypt(body: String?): String

    @Throws(Exception::class)
    fun decrypt(data: String?): String?
}