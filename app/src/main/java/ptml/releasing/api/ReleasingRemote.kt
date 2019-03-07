package ptml.releasing.api

import io.reactivex.Observable
import io.reactivex.Single

import ptml.releasing.db.models.response.base.BaseResponse
import retrofit2.Retrofit
import javax.inject.Inject
class ReleasingRemote @Inject constructor(retrofit: Retrofit): Remote{


    val deviceConfigService = retrofit.create(DeviceConfigService::class.java)

    override fun verifyDeviceId(imei: String):Single<BaseResponse> {
        return  deviceConfigService.verifyDeviceId(imei)
    }

}
