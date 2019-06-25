package ptml.releasing.app.utils


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import ptml.releasing.BuildConfig
import timber.log.Timber
import java.util.*

class RemoteConfigManger {
    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    private val defaults: Map<String, Any>
        get() {
            val map = HashMap<String, Any>()
            map[APP_CURRENT_VERSION] = BuildConfig.VERSION_CODE
            map[APP_MINIMUM_VERSION] = 1
            map[QUICK_REMARKS_CURRENT_VERSION] = 1
            map[DAMAGES_CURRENT_VERSION] = 1
            return map
        }

    val getAppCurrentVersion: Long
        get() = remoteConfig.getLong(APP_CURRENT_VERSION)


    val getAppMinimumVersion: Long
        get() = remoteConfig.getLong(APP_MINIMUM_VERSION)


    val getQuickRemarkCurrentVersion: Long
        get() = remoteConfig.getLong(QUICK_REMARKS_CURRENT_VERSION)

    val getDamagesCurrentVersion: Long
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
        }.addOnFailureListener {
            Timber.e(it)
            _loadingState.postValue(NetworkState.error(it))
        }

    }

    companion object {
        const val CACHE_EXPIRATION_SECS = 3600L
        const val APP_MINIMUM_VERSION = "app_version_minimum"
        const val APP_CURRENT_VERSION = "app_current_version"
        const val QUICK_REMARKS_CURRENT_VERSION = "quick_remarks_current_version"
        const val DAMAGES_CURRENT_VERSION = "damages_current_version"

        const val ERROR_MESSAGE =
            "No configs were fetched from the backend and the local fetched configs have already been activated"
    }


}
