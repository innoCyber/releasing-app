package ptml.releasing.app.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.securepreferences.SecurePreferences
import ptml.releasing.admin_configuration.models.AdminConfigResponse
import javax.inject.Inject

class PrefsManager @Inject constructor(var context: Context, var gson: Gson) : Prefs {
    companion object {
        const val PREFS = "prefs"
        const val FIRST = "is_first"
        const val CONFIG = "config"
    }

    private fun getPrefs(): SharedPreferences {
        return SecurePreferences(context, PREFS)
    }

    override fun isFirst(): Boolean {
        return getPrefs().getBoolean(FIRST, true)
    }

    override fun setFirst(value: Boolean) {
        return getPrefs().edit().putBoolean(FIRST, value).apply()
    }

    override fun saveConfig(response: AdminConfigResponse?) {
        getPrefs().edit().putString(CONFIG, gson.toJson(response)).apply()
    }

    override fun getConfig(): AdminConfigResponse? {
        return gson.fromJson(getPrefs().getString(CONFIG, "{}"), AdminConfigResponse::class.java)
    }
}
