package ptml.releasing.app.base

import com.google.gson.annotations.SerializedName

open class BaseResponse {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("success")
    var isSuccess: Boolean = false


    //used by serializers and deserializers
    constructor() {}

    constructor(message: String, success: Boolean) {
        this.message = message
        this.isSuccess = success
    }


}
