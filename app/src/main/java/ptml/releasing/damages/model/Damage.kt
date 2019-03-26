package ptml.releasing.damages.model

import com.google.gson.annotations.SerializedName

data class Damage(
    @SerializedName("Id") val id: Int,
    @SerializedName("Description") val description: String,
    @SerializedName("Position") val position: String,
    @SerializedName("TypeContainer") val typeContainer: Int
)