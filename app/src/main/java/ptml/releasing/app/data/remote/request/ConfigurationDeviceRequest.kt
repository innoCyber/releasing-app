package ptml.releasing.app.data.remote.request

import com.google.gson.annotations.SerializedName

/**
 * Created by kryptkode on 10/28/2019.
 */
data class ConfigurationDeviceRequest(
    @SerializedName("imei") val imei: String?,
    @SerializedName("operation_step") val operationType: Int,
    @SerializedName("terminal") val terminal: Int
)