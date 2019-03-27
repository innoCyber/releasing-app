package ptml.releasing.app.prefs

import android.content.SharedPreferences
import com.google.gson.Gson
import ptml.releasing.admin_configuration.models.AdminConfigResponse
import ptml.releasing.damages.model.DamageResponse
import javax.inject.Inject

class PrefsManager @Inject constructor(var sharedPreferences: SharedPreferences, var gson: Gson) : Prefs {
    companion object {
        const val FIRST = "is_first"
        const val CONFIG = "config"
        const val DAMAGES = "damages"
    }


    override fun isFirst(): Boolean {
        return sharedPreferences.getBoolean(FIRST, true)
    }

    override fun setFirst(value: Boolean) {
        return sharedPreferences.edit().putBoolean(FIRST, value).apply()
    }

    override fun saveConfig(response: AdminConfigResponse?) {
        sharedPreferences.edit().putString(CONFIG, gson.toJson(response)).apply()
    }

    override fun getConfig(): AdminConfigResponse? {
        return gson.fromJson(sharedPreferences.getString(CONFIG, null), AdminConfigResponse::class.java)
    }

    override fun saveDamages(response: DamageResponse?) {
        sharedPreferences.edit().putString(DAMAGES, gson.toJson(response)).apply()
    }

    override fun getDamages(): DamageResponse? {
        return gson.fromJson(sharedPreferences.getString(DAMAGES, null), DamageResponse::class.java)
    }
}
