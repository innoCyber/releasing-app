package ptml.releasing.app.utils.remoteconfig

import androidx.lifecycle.LiveData
import ptml.releasing.app.local.Local
import ptml.releasing.app.utils.NetworkState
import timber.log.Timber
import javax.inject.Inject

class RemoteConfigUpdateChecker @Inject constructor(
    val remoteConfigManger: RemoteConfigManger,
    private val versionCodeUtil: VersionCodeUtil,
    private val local: Local
) {
    val updateCheckState: LiveData<NetworkState> = remoteConfigManger.loadingState


    fun check() {
        remoteConfigManger.fetchAll()
    }

    fun mustUpdateApp(): Boolean {
        val localAppVersion = versionCodeUtil.getCurrentVersionCode()
        Timber.d("Current Version Code: $localAppVersion")
        val remoteAppVersion = remoteConfigManger.appMinimumVersion
        return shouldUpdate(localAppVersion, remoteAppVersion)

    }

    fun shouldUpdateApp(): Boolean {
        val localAppVersion = versionCodeUtil.getCurrentVersionCode()
        Timber.d("Current Version Code: $localAppVersion")
        val remoteAppMinimumVersion = remoteConfigManger.appCurrentVersion
        return shouldUpdate(localAppVersion, remoteAppMinimumVersion)
    }

    fun shouldUpdateQuickRemarks(): Boolean {
        val localQuickRemarkVersion = local.getQuickCurrentVersion()
        val remoteQuickRemarkVersion = remoteConfigManger.quickRemarkCurrentVersion
        return shouldUpdate(localQuickRemarkVersion, remoteQuickRemarkVersion)
    }

    fun shouldUpdateDamages(): Boolean {
        val localDamagesVersion = local.getDamagesCurrentVersion()
        val remoteDamagesVersion = remoteConfigManger.damagesCurrentVersion
        return shouldUpdate(localDamagesVersion, remoteDamagesVersion)
    }

    private fun shouldUpdate(localVersion: Long, remoteVersion: Long): Boolean {
        return remoteVersion > localVersion
    }


}