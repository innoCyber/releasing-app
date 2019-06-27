package ptml.releasing.app.utils.remoteconfig

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ptml.releasing.BuildConfig
import ptml.releasing.app.local.Local
import ptml.releasing.app.utils.NetworkState
import javax.inject.Inject

class RemoteConfigUpdateChecker @Inject constructor(
    val remoteConfigManger: RemoteConfigManger,
    private val local: Local
) {
    val updateCheckState: LiveData<NetworkState> = remoteConfigManger.loadingState


    fun check() {
        remoteConfigManger.fetchAll()
    }

    fun mustUpdateApp(): Boolean {
        val localAppVersion = BuildConfig.VERSION_CODE.toLong()
        val remoteAppVersion = remoteConfigManger.appMinimumVersion
        return shouldUpdate(localAppVersion, remoteAppVersion)

    }

    fun shouldUpdateApp(): Boolean {
        val localAppVersion = BuildConfig.VERSION_CODE.toLong()
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