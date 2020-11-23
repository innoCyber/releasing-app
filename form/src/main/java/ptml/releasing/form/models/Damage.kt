package ptml.releasing.form.models

import com.google.gson.annotations.SerializedName
import ptml.releasing.form.models.base.BaseModel

data class Damage(
    @SerializedName("description") val description: String,
    @SerializedName("position") val position: String,
    @SerializedName("typeContainer") val typeContainer: Int
) : BaseModel() {
    override fun toString(): String {
        return "Damage(id=$id, description='$description', position='$position', typeContainer=$typeContainer)"
    }
}