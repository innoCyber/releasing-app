package ptml.releasing.damages.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

data class Damage(
    @SerializedName("Id")override var id: Int,
    @SerializedName("Description") val description: String,
    @SerializedName("Position") val position: String,
    @SerializedName("TypeContainer") val typeContainer: Int
):BaseModel(){
    override fun toJson(): JsonObject {
        val obj = super.toJson()
        obj.addProperty("Id", id)
        obj.addProperty("Description", description)
        obj.addProperty("Position", position)
        obj.addProperty("TypeContainer", typeContainer)
        return obj
    }

    override fun toString(): String {
        return "Damage(id=$id, description='$description', position='$position', typeContainer=$typeContainer)"
    }


}