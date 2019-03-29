package ptml.releasing.app.base

import com.google.gson.annotations.SerializedName

import com.google.gson.JsonObject


open class BaseModel {
    @SerializedName("id")
    open var id: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseModel) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "BaseModel(id=$id)"
    }


}
