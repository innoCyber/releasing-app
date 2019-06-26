package ptml.releasing.app.utils.remoteconfig

import androidx.lifecycle.LiveData
import ptml.releasing.BuildConfig
import ptml.releasing.app.local.Local
import ptml.releasing.app.utils.NetworkState
import javax.inject.Inject

class RemoteConfigUpdateChecker @Inject constructor(
    private val configManger: RemoteConfigManger,
    private val localStore: Local
) {
    val updateCheckState:LiveData<NetworkState> = configManger.loadingState

    fun check(){
        configManger.fetchAll()
    }

    fun mustUpdateApp(): Boolean {
        val localAppVersion = BuildConfig.VERSION_CODE.toLong()
        val remoteAppVersion = configManger.appMinimumVersion
        return shouldUpdate(localAppVersion, remoteAppVersion)

    }

    fun shouldUpdateApp(): Boolean {
        val localAppVersion = BuildConfig.VERSION_CODE.toLong()
        val remoteAppMinimumVersion = configManger.appCurrentVersion
        return shouldUpdate(localAppVersion, remoteAppMinimumVersion)
    }

    fun shouldUpdateQuickRemarks(): Boolean {
        val localQuickRemarkVersion = localStore.getQuickCurrentVersion()
        val remoteQuickRemarkVersion = configManger.quickRemarkCurrentVersion
        return shouldUpdate(localQuickRemarkVersion, remoteQuickRemarkVersion)
    }

    fun shouldUpdateDamages(): Boolean {
        val localDamagesVersion = localStore.getDamagesCurrentVersion()
        val remoteDamagesVersion = configManger.damagesCurrentVersion
        return shouldUpdate(localDamagesVersion, remoteDamagesVersion)
    }

    private fun shouldUpdate(localVersion: Long, remoteVersion: Long): Boolean {
        return remoteVersion > localVersion
    }

}