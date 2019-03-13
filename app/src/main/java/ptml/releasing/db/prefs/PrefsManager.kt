package ptml.releasing.db.prefs

import android.content.Context
import android.content.SharedPreferences
import com.securepreferences.SecurePreferences
import javax.inject.Inject

class PrefsManager @Inject constructor(var context: Context):Prefs {
    companion object {
        const val PREFS = "prefs"
        const val FIRST = "is_first"
    }

    fun getPrefs(): SharedPreferences {
        return SecurePreferences(context, PREFS)
    }

   override fun isFirst(): Boolean {
       return getPrefs().getBoolean(FIRST, true)
   }

    override fun setFirst(value:Boolean) {
        return getPrefs().edit().putBoolean(FIRST, value).apply()
    }



}
