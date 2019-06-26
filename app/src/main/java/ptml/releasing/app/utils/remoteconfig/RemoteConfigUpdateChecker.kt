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

    fun shouldUpdateApp(): Boolean {
        val localAppVersion = BuildConfig.VERSION_CODE.toLong()
        val remoteAppVersion = configManger.appCurrentVersion
        return mustUpdate(localAppVersion, remoteAppVersion)
    }

    fun shouldUpdateQuickRemarks(): Boolean {
        val localQuickRemarkVersion = localStore.getQuickCurrentVersion()
        val remoteQuickRemarkVersion = configManger.quickRemarkCurrentVersion
        return mustUpdate(localQuickRemarkVersion, remoteQuickRemarkVersion)
    }

    fun shouldUpdateDamages(): Boolean {
        val localDamagesVersion = localStore.getDamagesCurrentVersion()
        val remoteDamagesVersion = configManger.damagesCurrentVersion
        return mustUpdate(localDamagesVersion, remoteDamagesVersion)
    }

    private fun mustUpdate(localVersion: Long, remoteVersion: Long): Boolean {
        return remoteVersion > localVersion
    }
}