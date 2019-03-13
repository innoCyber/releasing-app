package ptml.releasing.api

import io.reactivex.Observable
import ptml.releasing.db.models.base.BaseResponse
import retrofit2.Retrofit
import javax.inject.Inject

class ReleasingRemote @Inject constructor(var retrofit: Retrofit) : Remote {

    private val deviceConfigService = retrofit.create(ApiService::class.java)

    override fun login(username: String?, password: String?) = deviceConfigService.login(username, password)


    override fun verifyDeviceId(imei: String) = deviceConfigService.verifyDeviceId(imei)

    override fun setAdminConfiguration(imei: String) = deviceConfigService.setAdminConfiguration(imei)

}
