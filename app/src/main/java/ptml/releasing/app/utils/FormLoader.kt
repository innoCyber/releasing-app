package ptml.releasing.app.utils

import android.content.Context
import com.google.gson.Gson
import ptml.releasing.configuration.models.ConfigureDeviceResponse

object FormLoader {
    fun loadFromAssets(context: Context): ConfigureDeviceResponse?{
        val assetHelper = AssetHelper()
        val data = assetHelper.getAssetFileContents(context, "set_admin_config.json")
        return Gson().fromJson(data, ConfigureDeviceResponse::class.java)
    }
}