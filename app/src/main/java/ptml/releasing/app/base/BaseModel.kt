package ptml.releasing.app.base

import com.google.gson.annotations.SerializedName

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonObject

@Entity
open class BaseModel {
    @SerializedName("id")
    @PrimaryKey
    var id: Int = 0

    open fun toJson(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        return jsonObject
    }
}
