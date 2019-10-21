package ptml.releasing.app.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import ptml.releasing.BuildConfig
import ptml.releasing.app.utils.Constants
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemarkResponse
import javax.inject.Inject

class PrefsManager @Inject constructor(var sharedPreferences: SharedPreferences, var gson: Gson, var context: Context) :
    Prefs {
    companion object {
        const val FIRST = "is_first"
        const val CONFIG = "config"
        const val CONFIGURED = "configured"
        const val DAMAGES = "damages"
        const val SAVED_CONFIG = "saved_config"
        const val DEVICE_CONFIG = "device_config"
        const val SETTINGS = "settings"
        const val OPERATOR_NAME = "operator_name"
        const val SERVER_URL = "server_url"
        const val QUICK_REMARK = "quick_remark"

        const val APP_VERSION = "app_version_minimum"
        const val QUICK_REMARKS_VERSION = "quick_remarks_current_version"
        const val DAMAGES_VERSION = "damages_current_version"
        const val MUST_UPDATE_APP = "must_update_app"
        const val IMEI = "imei"
        const val INTERNET_ERROR_LOGGING_ENABLED = "internet_error_logging_enabled"
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

    override fun getSavedConfig(): Configuration {
        return gson.fromJson(sharedPreferences.getString(SAVED_CONFIG, "{}"), Configuration::class.java)
    }

    override fun setSavedConfig(configuration: Configuration) {
        sharedPreferences.edit().putString(SAVED_CONFIG, gson.toJson(configuration)).apply()
    }

    override fun isConfigured(): Boolean {
        return sharedPreferences.getBoolean(CONFIGURED, false)
    }

    override fun setConfigured(isConfigured: Boolean) {
        return sharedPreferences.edit().putBoolean(CONFIGURED, isConfigured).apply()
    }

    override fun getDeviceConfiguration(): ConfigureDeviceResponse? {
        return gson.fromJson(sharedPreferences.getString(DEVICE_CONFIG, "{}"), ConfigureDeviceResponse::class.java)
//        return FormLoader.loadFromAssets(context)
    }

    override fun saveDeviceConfiguration(response: ConfigureDeviceResponse?) {
        sharedPreferences.edit().putString(DEVICE_CONFIG, gson.toJson(response)).apply()
    }

    override fun saveSettings(settings: Settings?) {
        sharedPreferences.edit().putString(SETTINGS, gson.toJson(settings)).apply()
    }

    override fun getSettings(): Settings {
        return gson.fromJson(sharedPreferences.getString(SETTINGS, "{}"), Settings::class.java)
    }

    override fun getOperatorName(): String? {
        return sharedPreferences.getString(OPERATOR_NAME, null)
    }

    override fun saveOperatorName(name: String?) {
        sharedPreferences.edit().putString(OPERATOR_NAME, name).apply()
    }

    override fun saveServerUrl(url: String?) {
        sharedPreferences.edit().putString(SERVER_URL, url).apply()
    }

    override fun getServerUrl(): String? {
        return sharedPreferences.getString(SERVER_URL, BuildConfig.BASE_URL)
    }

    override fun saveQuickRemarks(response: QuickRemarkResponse?) {
        sharedPreferences.edit().putString(QUICK_REMARK, gson.toJson(response)).apply()
    }

    override fun getQuickRemarks(): QuickRemarkResponse? {
        return gson.fromJson(sharedPreferences.getString(QUICK_REMARK, null), QuickRemarkResponse::class.java)
    }

    override fun setDamagesCurrentVersion(currentVersion: Long) {
        sharedPreferences.edit().putLong(DAMAGES_VERSION, currentVersion).apply()
    }

    override fun getDamagesCurrentVersion(): Long {
        return sharedPreferences.getLong(DAMAGES_VERSION, Constants.DEFAULT_DAMAGES_VERSION)
    }

    override fun setQuickCurrentVersion(currentVersion: Long) {
        sharedPreferences.edit().putLong(QUICK_REMARKS_VERSION, currentVersion).apply()
    }

    override fun getQuickCurrentVersion(): Long {
        return sharedPreferences.getLong(QUICK_REMARKS_VERSION, Constants.DEFAULT_QUICK_REMARKS_VERSION)
    }

    override fun setAppVersion(version: Long) {
        sharedPreferences.edit().putLong(APP_VERSION, version).apply()
    }

    override fun getAppVersion(): Long {
        return sharedPreferences.getLong(APP_VERSION, Constants.DEFAULT_APP_VERSION)
    }

    override fun setImei(imei: String) {
        return sharedPreferences.edit().putString(IMEI, imei).apply()
    }

    override fun getImei(): String? {
        return sharedPreferences.getString(IMEI, null)
    }

    override fun mustUpdateApp(): Boolean {
        return sharedPreferences.getBoolean(MUST_UPDATE_APP, false)
    }

    override fun setUpdateApp(shouldUpdate: Boolean) {
        return sharedPreferences.edit().putBoolean(MUST_UPDATE_APP, shouldUpdate).apply()
    }


    override fun isInternetErrorLoggingEnabled(): Boolean {
        return sharedPreferences.getBoolean(INTERNET_ERROR_LOGGING_ENABLED, false)
    }

    override fun setInternetErrorLoggingEnabled(enabled: Boolean) {
        return sharedPreferences.edit().putBoolean(INTERNET_ERROR_LOGGING_ENABLED, enabled).apply()
    }
}
