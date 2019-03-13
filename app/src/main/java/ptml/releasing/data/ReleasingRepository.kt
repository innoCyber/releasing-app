package ptml.releasing.data

import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.functions.Function3
import ptml.releasing.api.ReleasingRemote
import ptml.releasing.api.Remote
import ptml.releasing.db.Local
import ptml.releasing.db.ReleasingLocal
import ptml.releasing.db.models.User
import ptml.releasing.db.models.config.response.ConfigurationResponse
import timber.log.Timber
import javax.inject.Inject

class ReleasingRepository @Inject constructor(var remote: Remote, var local: Local) : Repository {

    override fun verifyDeviceId(imei: String) = remote.verifyDeviceId(imei)
    override fun login(user: User) = remote.login(user.username, user.password)


    override fun getAdminConfiguration(imei: String): Observable<ConfigurationResponse> {
        return Observable.concatArrayEager(localConfig()
            ,remoteConfiguration(imei))
    }

    private fun localConfig(): Observable<ConfigurationResponse> {
        return Observable.combineLatest(local.getCargoTypes(), local.getOperationSteps(), local.getTerminals(),
            Function3 { cargoTypes, operationSteps, terminals ->
                Timber.d("Getting local data")
                ConfigurationResponse("", true,
                    cargoTypes,
                    operationSteps,
                    terminals
                )
            })
    }

    private fun remoteConfiguration(imei: String): Observable<ConfigurationResponse> {
        Timber.d("Getting config  from server")
        return remote.setAdminConfiguration(imei)
            .flatMap{saveObservable(it)}

    }

    private fun saveConfig(response: ConfigurationResponse): Observable<Unit> {
        Timber.d("Saving config")
        return Observable.concatArrayEager(local.insertCargoTypes(response.cargoTypeList)
            ,local.insertOperationSteps(response.operationStepList),
            local.insertTerminals(response.terminalList))
    }

    private fun saveObservable(response: ConfigurationResponse): Observable<ConfigurationResponse>{
        return Observable.fromArray(saveConfig(response))
            .map { response }
    }

}