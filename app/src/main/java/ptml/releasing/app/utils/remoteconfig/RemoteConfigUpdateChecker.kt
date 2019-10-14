package ptml.releasing.app.utils.remoteconfig

import androidx.lifecycle.LiveData
import ptml.releasing.BuildConfig
import ptml.releasing.app.local.Local
import ptml.releasing.app.utils.NetworkState
import timber.log.Timber
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
        val remoteAppVersion = remoteConfigManger.appVersion
        return shouldUpdate(localAppVersion, remoteAppVersion)

    }

    fun shouldUpdateQuickRemarks(): Boolean {
        val localQuickRemarkVersion = local.getQuickRemarksVersion()
        val remoteQuickRemarkVersion = remoteConfigManger.quickRemarkVersion
        Timber.d("shouldUpdateQuickRemarks(L-R): $localQuickRemarkVersion - $remoteQuickRemarkVersion")
        return shouldUpdate(localQuickRemarkVersion, remoteQuickRemarkVersion)
    }

    fun shouldUpdateDamages(): Boolean {
        val localDamagesVersion = local.getDamagesVersion()
        val remoteDamagesVersion = remoteConfigManger.damagesVersion
        Timber.d("shouldUpdateDamages(L-R): $localDamagesVersion - $remoteDamagesVersion")
        return shouldUpdate(localDamagesVersion, remoteDamagesVersion)
    }

    private fun shouldUpdate(localVersion: Long, remoteVersion: Long): Boolean {
        return remoteVersion > localVersion
    }


}