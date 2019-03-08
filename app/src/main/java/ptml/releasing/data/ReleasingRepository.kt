package ptml.releasing.data

import ptml.releasing.api.ReleasingRemote
import javax.inject.Inject

class ReleasingRepository @Inject constructor(var remote: ReleasingRemote){


    fun verifyDeviceId( imei: String) = remote.verifyDeviceId(imei)
}