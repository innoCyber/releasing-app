package ptml.releasing.app.data.remote.request

import com.google.gson.annotations.SerializedName

/**
 * Created by kryptkode on 10/23/2019.
 */

data class LoginRequest(
    @SerializedName("badge_id") val badgeId: String,
    @SerializedName("password") val password: String,
    @SerializedName("imei") val imei: String?,
    @SerializedName("operation_type") val operationType: Int,
    @SerializedName("terminal") val terminal: Int
)