package ptml.releasing.damages.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

data class Damage(
    @SerializedName("description") val description: String,
    @SerializedName("position") val position: String,
    @SerializedName("typecontainer") val typeContainer: Int
):BaseModel(){
    override fun toString(): String {
        return "Damage(id=$id, description='$description', position='$position', typeContainer=$typeContainer)"
    }


}