package ptml.releasing.app.utils.remoteconfig


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import ptml.releasing.BuildConfig
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.NetworkState
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class RemoteConfigManger @Inject constructor() {
    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    private val defaults: Map<String, Any>
        get() {
            val map = HashMap<String, Any>()
            map[APP_CURRENT_VERSION] = BuildConfig.VERSION_CODE
            map[APP_MINIMUM_VERSION] =
                Constants.DEFAULT_APP_MINIMUM_VERSION
            map[QUICK_REMARKS_CURRENT_VERSION] =
                Constants.DEFAULT_QUICK_REMARKS_VERSION
            map[DAMAGES_CURRENT_VERSION] =
                Constants.DEFAULT_DAMAGES_VERSION
            return map
        }

    val appCurrentVersion: Long
        get() = remoteConfig.getLong(APP_CURRENT_VERSION)


    val appMinimumVersion: Long
        get() = remoteConfig.getLong(APP_MINIMUM_VERSION)


    val quickRemarkCurrentVersion: Long
        get() = remoteConfig.getLong(QUICK_REMARKS_CURRENT_VERSION)

    val damagesCurrentVersion: Long
        get() = remoteConfig.getLong(DAMAGES_CURRENT_VERSION)

    private val _loadingState = MutableLiveData<NetworkState>()
    val loadingState: LiveData<NetworkState> = _loadingState

    init {
        var cacheExpiration = CACHE_EXPIRATION_SECS
        if (BuildConfig.DEBUG) {
            cacheExpiration = 0L
        }
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(cacheExpiration)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.d("Successfully set remote config settings")
            } else {
                Timber.e("Error setting remote config settings")
            }
        }
        remoteConfig.setDefaultsAsync(defaults).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.d("Successfully set remote config defaults")
            } else {
                Timber.e("Error setting remote config defaults")
            }
        }
    }


    fun fetchAll() {
        if (_loadingState.value == NetworkState.LOADING) {
            Timber.d("Already fetching remote config data...")
            return
        }

        _loadingState.postValue(NetworkState.LOADING)
        remoteConfig.fetchAndActivate().addOnSuccessListener { success ->
            if (success) {
                _loadingState.postValue(NetworkState.LOADED)
            } else {
                _loadingState.postValue(NetworkState.error(ERROR_MESSAGE))
            }
            logValues()
        }.addOnFailureListener {
            Timber.e(it)
            _loadingState.postValue(NetworkState.error(it))
            logValues()
        }

    }

    private fun logValues() {
        Timber.d("DAMAGES: $damagesCurrentVersion, QUICK: $quickRemarkCurrentVersion, APP_CURRENT: $appCurrentVersion, APP_MINIMUM: $appMinimumVersion")
    }

    companion object {
        const val CACHE_EXPIRATION_SECS = 3600L
        const val APP_CURRENT_VERSION = "app_current_version"
        const val APP_MINIMUM_VERSION = "app_version_minimum"
        const val QUICK_REMARKS_CURRENT_VERSION = "quick_remarks_current_version"
        const val DAMAGES_CURRENT_VERSION = "damages_current_version"

        const val ERROR_MESSAGE =
            "No configs were fetched from the backend and the local fetched configs have already been activated"
    }


}
