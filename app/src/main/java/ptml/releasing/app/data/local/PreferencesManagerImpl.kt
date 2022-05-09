package ptml.releasing.app.data.local

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ptml.releasing.BuildConfig
import ptml.releasing.app.ReleasingApplication
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

    override fun getAuthKey(): String {
        val loginData = gson.fromJson(getStringPreference(prefLoginData), LoginEntity::class.java)
         val authPayload = "${loginData.badgeId}:${loginData.password}"
        val data = authPayload.toByteArray()
         val base64 = Base64.encodeToString(data, Base64.NO_WRAP)
        return "Basic $base64".trim()
    }

    override fun getStaticAuth(): String{
        val _mPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ReleasingApplication.appContext)
        val BASEURL = _mPreferences.getString("BASE_URL_RELEASING", "")
        var username: String? = null
        var password : String? = null
        var baseurl = BASEURL

        if (baseurl.isNullOrEmpty()|| baseurl.isNullOrBlank() || baseurl == null) {
            baseurl = BuildConfig.BASE_URL
        }

        if (baseurl.contains("1448")) {
            username = "Ptml01R1"
            password = "SPtml0309!!"
        }

        if (baseurl.contains("8085")) {
            username = "admin"
            password = "Passw2021"
        }

        if (baseurl.contains("1449")) {
            username = "admin"
            password = "Passw2021"

        }

        val authPayload = "$username:$password"
        val data = authPayload.toByteArray()
        val base64 = Base64.encodeToString(data, Base64.NO_WRAP)

        return "Basic $base64".trim()

    }
//
//    override fun getStaticAuth(): String {
//        val username = if(BuildConfig.FLAVOR == "production" || BuildConfig.FLAVOR == "staging") "Ptml01R1" else "admin"
//        val password = if(BuildConfig.FLAVOR == "production" || BuildConfig.FLAVOR == "staging" ) "SPtml0309!!" else "Passw2021"
//        val authPayload = "$username:$password"
//        val data = authPayload.toByteArray()
//        val base64 = Base64.encodeToString(data, Base64.NO_WRAP)
//        return "Basic $base64".trim()
//    }

    override fun getIMEI(): String {
        return getStringPreference(prefImei, "")
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