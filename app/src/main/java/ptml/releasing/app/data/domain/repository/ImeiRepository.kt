package ptml.releasing.app.data.domain.repository

/**
 * Created by kryptkode on 11/14/2019.
 */
interface ImeiRepository {
    suspend fun getIMEI(): String
    suspend fun setIMEI(imei: String)
}