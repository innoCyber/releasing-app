package ptml.releasing.app.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ptml.releasing.BuildConfig
import ptml.releasing.app.data.domain.model.login.LoginEntity
import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */

open class PreferencesManagerImpl @Inject constructor(
    private val gson: Gson,
    sharedPreferences: SharedPreferences
) : BasePreferencesManager(sharedPreferences) {


    override fun getLoginData(): LoginEntity {
        return gson.fromJson(getStringPreference(prefLoginData), LoginEntity::class.java)
    }

    override fun setLoginData(entity: LoginEntity) {
        return setStringPreference(prefLoginData, gson.toJson(entity))
    }

    override fun clearLoginData() {
        removePreference(prefLoginData)
    }

    override fun loggedIn(): Boolean {
        return getBooleanPreference(prefLoggedIn)
    }

    override fun setLoggedIn(value: Boolean) {
        return setBooleanPreference(prefLoggedIn, value)
    }

    override fun getIMEI(): String {
        return getStringPreference(prefImei)
    }

    override fun setIMEI(imei: String) {
        return setStringPreference(prefImei, imei)
    }


    override fun getServerBaseUrl(): String {
        return getStringPreference(prefBaseServerUrl, BuildConfig.BASE_URL)
    }

    override fun setServerBaseUrl(url: String) {
        return setStringPreference(prefBaseServerUrl, url)
    }

    override fun getLastSelectedVoyage(): ReleasingVoyage? {
        return gson.fromJson(
            getStringPreference(prefLastSelectedVoyage, ""),
            ReleasingVoyage::class.java
        )
    }

    override fun setLastSelectedVoyage(voyage: ReleasingVoyage) {
        return setStringPreference(prefLastSelectedVoyage, gson.toJson(voyage))
    }

    override fun getRecentVoyages(): List<ReleasingVoyage> {
        return gson.fromJson(
            getStringPreference(prefRecentVoyages, "[]"),
            object : TypeToken<List<ReleasingVoyage>>() {}.type
        )
    }

    override fun setRecentVoyages(voyages: List<ReleasingVoyage>) {
        return setStringPreference(prefRecentVoyages, gson.toJson(voyages))
    }

    override fun getLastActiveTime(): Long {
        return getLongPreference(prefLastActiveTime)
    }

    override fun setLastActiveTime(time: Long) {
        return setLongPreference(prefLastActiveTime, time)
    }
}