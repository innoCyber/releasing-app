package ptml.releasing.resetpassword.model

import ptml.releasing.app.data.remote.request.ResetPasswordRequest
import ptml.releasing.app.data.remote.result._Result

/**
 * Created by kryptkode on 10/23/2019.
 */
interface ResetPasswordDataSource {
    interface Remote {
        suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): _Result<Unit>
    }
}