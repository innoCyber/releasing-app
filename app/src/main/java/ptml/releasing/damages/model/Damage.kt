package ptml.releasing.damages.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

data class Damage(
    @SerializedName("description") val description: String,
    @SerializedName("position") val position: String,
    @SerializedName("typecontainer") val typeContainer: Int
):BaseModel(){
    override fun toJson(): JsonObject {
        val obj = super.toJson()
        obj.addProperty("id", id)
        obj.addProperty("description", description)
        obj.addProperty("position", position)
        obj.addProperty("typecontainer", typeContainer)
        return obj
    }

    override fun toString(): String {
        return "Damage(id=$id, description='$description', position='$position', typeContainer=$typeContainer)"
    }


}