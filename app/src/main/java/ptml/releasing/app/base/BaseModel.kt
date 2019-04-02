package ptml.releasing.app.base

import com.google.gson.annotations.SerializedName

import com.google.gson.JsonObject


open class BaseModel {
    @SerializedName("id")
    open var id: Int = 0

    open fun toJson(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", id)
        return jsonObject
    }
}
