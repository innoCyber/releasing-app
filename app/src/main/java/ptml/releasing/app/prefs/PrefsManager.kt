package ptml.releasing.app.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ptml.releasing.BuildConfig
import ptml.releasing.app.utils.Constants
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.images.model.Image
import ptml.releasing.printer.model.Settings
import ptml.releasing.quick_remarks.model.QuickRemarkResponse
import timber.log.Timber
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
        const val PRINTER_SETTINGS = "settings"
        const val OPERATOR_NAME = "operator_name"
        const val SERVER_URL = "server_url"
        const val QUICK_REMARK = "quick_remark"

        const val APP_VERSION = "app_version_minimum"
        const val QUICK_REMARKS_VERSION = "quick_remarks_current_version"
        const val DAMAGES_VERSION = "damages_current_version"
        const val VOYAGE_VERSION = "voyage_current_version"
        const val MUST_UPDATE_APP = "must_update_app"
        const val INTERNET_ERROR_LOGGING_ENABLED = "internet_error_logging_enabled"
        const val WORKER_ID = "_workerId"
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

    override fun savePrinterSettings(settings: Settings?) {
        sharedPreferences.edit().putString(PRINTER_SETTINGS, gson.toJson(settings)).apply()
    }

    override fun getPrinterSettings(): Settings {
        return gson.fromJson(
            sharedPreferences.getString(PRINTER_SETTINGS, "{}"),
            Settings::class.java
        )
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

    override fun getVoyageVersion(): Long {
        return sharedPreferences.getLong(VOYAGE_VERSION, Constants.DEFAULT_VOYAGE_VERSION)
    }

    override fun setVoyageCurrentVersion(currentVersion: Long) {
        sharedPreferences.edit().putLong(VOYAGE_VERSION, currentVersion).apply()
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

    override fun getImages(cargoCode: String):  Map<String, Image> {
        Timber.w("getImages")
        return gson.fromJson(
            sharedPreferences.getString(cargoCode, "[]"),
           object : TypeToken<Map<String, Image>>(){}.type
        )
    }

    override fun storeImages(cargoCode: String, imageMap:  Map<String, Image>) {
        sharedPreferences.edit().putString(cargoCode, gson.toJson(imageMap)).apply()
    }

    override fun addImage(cargoCode: String, file: Image) {
        val images = getImages(cargoCode).toMutableMap()
        images[file.name ?: return] = file
        storeImages(cargoCode, images)
    }

    override fun removeImage(cargoCode: String, file: Image) {
        val images = getImages(cargoCode).toMutableMap()
        images.remove(file.name ?: return)
        storeImages(cargoCode, images)
    }

    override fun addWorkerId(cargoCode: String, workerId:String) {
        sharedPreferences.edit().putString("$cargoCode$WORKER_ID", workerId).apply()
    }

    override fun getWorkerId(cargoCode: String): String? {
        return sharedPreferences.getString("$cargoCode$WORKER_ID", null)
    }
}
