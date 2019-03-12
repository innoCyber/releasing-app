package ptml.releasing.db.prefs

import android.content.SharedPreferences

interface Prefs {
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
}