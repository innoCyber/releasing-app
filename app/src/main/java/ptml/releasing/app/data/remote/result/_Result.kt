package ptml.releasing.app.data.remote.result

import com.google.gson.annotations.SerializedName

/**
 * Created by kryptkode on 10/23/2019.
 */

data class _Result<T>(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?
) {
    constructor(success: Boolean?, message: String?) : this(success, null, message)
}