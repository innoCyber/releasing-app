package ptml.releasing.resetpassword.model

import ptml.releasing.app.data.remote.RestClient
import ptml.releasing.app.data.remote.request.ResetPasswordRequest
import ptml.releasing.app.data.remote.result._Result
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */
class ResetPasswordRemoteDataSource @Inject constructor(
    private val restClient: RestClient
) : ResetPasswordDataSource.Remote {

    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): _Result<Unit> {
        //Api weirdly expect an Array of @ResetPasswordRequest object
        return restClient.getRemoteCaller().resetPassword(listOf(resetPasswordRequest))
    }
}