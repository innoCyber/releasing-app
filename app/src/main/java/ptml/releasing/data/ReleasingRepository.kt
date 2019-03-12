package ptml.releasing.data

import io.reactivex.Observable
import ptml.releasing.api.ReleasingRemote
import ptml.releasing.db.models.User
import ptml.releasing.db.models.response.base.BaseResponse
import javax.inject.Inject

class ReleasingRepository @Inject constructor(var remote: ReleasingRemote) : Repository{
    override fun verifyDeviceId( imei: String) = remote.verifyDeviceId(imei)
    override fun login(user: User) = remote.login(user.username, user.password)
}