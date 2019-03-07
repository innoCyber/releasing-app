package ptml.releasing.data

import ptml.releasing.api.ReleasingRemote
import javax.inject.Inject
import javax.inject.Named

class ReleasingRepository @Inject constructor(var remote: ReleasingRemote){


    fun verifyDeviceId(@Named("deviceId") imei: String) = remote.verifyDeviceId(imei)
}