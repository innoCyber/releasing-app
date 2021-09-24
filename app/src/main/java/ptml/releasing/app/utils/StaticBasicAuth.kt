package ptml.releasing.app.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Base64
import ptml.releasing.app.ReleasingApplication

object StaticBasicAuth {
    private val prefs = PreferenceManager
        .getDefaultSharedPreferences(ReleasingApplication.appContext)
    private val edit: SharedPreferences.Editor = prefs.edit()

    private val mPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(ReleasingApplication.appContext)

    fun saveAdminLoginDetails(username: String?, password: String?) {
        edit.putString("username", username)
        edit.putString("password", password)
        edit.commit()
    }
    
    fun getAdminPassword(): String?{
        return mPreferences.getString("password", "")
    }
    
    fun getAdminUsername():String?{
        return mPreferences.getString("username", "")
    }
    
    fun getAdminBasicAuth(username:String, password: String?): String{
        val authPayload = "${username}:${password}"
        val data = authPayload.toByteArray()
        val base64 = Base64.encodeToString(data, Base64.NO_WRAP)
        return "Basic $base64".trim()
    }

}