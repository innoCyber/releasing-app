package ptml.releasing.data

import io.reactivex.Observable
import io.reactivex.functions.Function3
import ptml.releasing.api.ReleasingRemote
import ptml.releasing.api.Remote
import ptml.releasing.db.Local
import ptml.releasing.db.ReleasingLocal
import ptml.releasing.db.models.User
import ptml.releasing.db.models.config.response.ConfigurationResponse
import javax.inject.Inject

class ReleasingRepository @Inject constructor(var remote: Remote, var local: Local) : Repository {

    override fun verifyDeviceId(imei: String) = remote.verifyDeviceId(imei)
    override fun login(user: User) = remote.login(user.username, user.password)


    override fun getAdminConfiguration(imei: String): Observable<ConfigurationResponse> {
        return localConfig()
                .concatWith (remoteConfiguration(imei))
    }

    private fun localConfig(): Observable<ConfigurationResponse> {
        return Observable.combineLatest(local.getCargoTypes(), local.getOperationSteps(), local.getTerminals(),
                Function3 { cargoTypes, operationSteps, terminals -> ConfigurationResponse(cargoTypes, operationSteps, terminals) })
    }
    private fun remoteConfiguration(imei: String): Observable<ConfigurationResponse> {
        return remote.setAdminConfiguration(imei)
                .doOnNext { saveConfig(it) }
    }

    private fun saveConfig(response: ConfigurationResponse): Observable<Unit>? {
        return local.insertCargoTypes(response.cargoTypeList)
                .concatWith(local.insertOperationSteps(response.operationStepList))
                .concatWith(local.insertTerminals(response.terminalList))
    }

}