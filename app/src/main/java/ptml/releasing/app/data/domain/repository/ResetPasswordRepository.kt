package ptml.releasing.app.data.domain.repository

import ptml.releasing.app.data.domain.model.ApiResult

/**
 * Created by kryptkode on 10/23/2019.
 */
interface ResetPasswordRepository {
    suspend fun resetPassword(
        badgeId: String,
        password: String,
        imei: String
    ): ApiResult
}