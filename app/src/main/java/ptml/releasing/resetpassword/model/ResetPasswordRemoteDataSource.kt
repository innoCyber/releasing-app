package ptml.releasing.resetpassword.model

import ptml.releasing.app.data.remote.AuthRestClient
import ptml.releasing.app.data.remote.request.ResetPasswordRequest
import ptml.releasing.app.data.remote.result._Result
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */
class ResetPasswordRemoteDataSource @Inject constructor(
    private val authRestClient: AuthRestClient
) : ResetPasswordDataSource.Remote {

    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): _Result<Unit> {
        return authRestClient.getRemoteCaller().resetPassword(resetPasswordRequest)
    }
}