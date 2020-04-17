package ptml.releasing.app.data.remote.result

import com.google.gson.annotations.SerializedName

/**
 * Created by kryptkode on 4/8/2020.
 */
data class VoyageRemote(
    @SerializedName("ID_VOYAGE") val id: Int,
    @SerializedName("VESSEL_NAME") val vesselName: String
)