package ptml.releasing.app.data.domain.model.login

import com.google.gson.annotations.SerializedName

/**
 * Created by kryptkode on 10/23/2019.
 */

data class LoginEntity(
    @SerializedName("badge_id") val badgeId: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("imei") val imei: String?
)