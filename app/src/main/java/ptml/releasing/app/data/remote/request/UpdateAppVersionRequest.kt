package ptml.releasing.app.data.remote.request

import com.google.gson.annotations.SerializedName

/**
 * Created by kryptkode on 10/23/2019.
 */

data class UpdateAppVersionRequest(
    @SerializedName("app_version") val appVersion: String,
    @SerializedName("imei") val imei: String?
)