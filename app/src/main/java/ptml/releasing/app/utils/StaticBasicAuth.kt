package ptml.releasing.app.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Base64
import ptml.releasing.BuildConfig
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

    fun getURL():String{
        val _mPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(
            ReleasingApplication.appContext)
        var baseurl = _mPreferences.getString("BASE_URL_RELEASING", "")

        if ( baseurl.isNullOrEmpty()|| baseurl.isNullOrBlank() || baseurl == null){
            baseurl = BuildConfig.BASE_URL
        }

        return baseurl

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