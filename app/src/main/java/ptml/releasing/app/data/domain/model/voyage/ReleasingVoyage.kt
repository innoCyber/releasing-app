package ptml.releasing.app.data.domain.model.voyage

import com.google.gson.annotations.SerializedName

/**
 * Created by kryptkode on 4/8/2020.
 */
data class ReleasingVoyage(
    @SerializedName("VOYAGE_NUMBER") val voyageNumber: String
)