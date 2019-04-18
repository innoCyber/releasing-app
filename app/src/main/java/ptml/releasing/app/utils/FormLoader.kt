package ptml.releasing.app.utils

import android.content.Context
import com.google.gson.Gson
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.configuration.models.ConfigureDeviceResponse

object FormLoader {
    fun loadFromAssets(context: Context): ConfigureDeviceResponse?{
        val assetHelper = AssetHelper()
        val data = assetHelper.getAssetFileContents(context, "cargo_info.json")
        return Gson().fromJson(data, ConfigureDeviceResponse::class.java)
    }

    fun loadFindCargoResponseFromAssets(context: Context): FindCargoResponse? {
        val assetHelper = AssetHelper()
        val data = assetHelper.getAssetFileContents(context, "find_cargo.json")
        return Gson().fromJson(data, FindCargoResponse::class.java)
    }
}