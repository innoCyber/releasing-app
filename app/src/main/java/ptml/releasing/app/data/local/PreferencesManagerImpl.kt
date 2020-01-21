package ptml.releasing.app.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import ptml.releasing.BuildConfig
import ptml.releasing.app.data.domain.model.login.AdminOptionsEntity
import ptml.releasing.app.data.domain.model.login.LoginEntity
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

    override fun loggedIn(): Boolean {
        return getBooleanPreference(prefLoggedIn)
    }

    override fun setLoggedIn(value: Boolean) {
        return setBooleanPreference(prefLoggedIn, value)
    }

    override fun getAdminOptions(): AdminOptionsEntity {
        return gson.fromJson(getStringPreference(prefAdminOptions), AdminOptionsEntity::class.java)
    }

    override fun setAdminOptions(entity: AdminOptionsEntity) {
        return setStringPreference(prefAdminOptions, gson.toJson(entity))
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

}