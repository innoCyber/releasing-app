package ptml.releasing.app.data.domain.model.voyage

import com.google.gson.annotations.SerializedName

/**
 * Created by kryptkode on 4/8/2020.
 */
data class ReleasingVoyage(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val vesselName: String
)