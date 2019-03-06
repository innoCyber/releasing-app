package ptml.releasing.db

import com.google.gson.annotations.SerializedName

data class Device (@SerializedName("imei") val imei:String)

data class LoginRequest (@SerializedName("username") val username:String,
                         @SerializedName("password") val password:String)