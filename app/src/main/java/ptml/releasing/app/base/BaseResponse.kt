package ptml.releasing.app.base

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

open class BaseResponse :AppResponse{
    @SerializedName("message")
    var message: String? = null

    @SerializedName("success")
    var isSuccess: Boolean = false


    //used by serializers and deserializers
    constructor()

    constructor(message: String, success: Boolean) {
        this.message = message
        this.isSuccess = success
    }




    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseResponse) return false

        if (message != other.message) return false
        if (isSuccess != other.isSuccess) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message?.hashCode() ?: 0
        result = 31 * result + isSuccess.hashCode()
        return result
    }

    override fun toString(): String {
        return "BaseResponse(message=$message, isSuccess=$isSuccess)"
    }
}
