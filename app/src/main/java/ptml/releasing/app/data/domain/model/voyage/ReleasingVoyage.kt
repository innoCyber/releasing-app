package ptml.releasing.app.data.domain.model.voyage

import com.google.gson.annotations.SerializedName
import ptml.releasing.configuration.models.BaseConfig

/**
 * Created by kryptkode on 4/8/2020.
 */
data class ReleasingVoyage(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val vesselName: String
)