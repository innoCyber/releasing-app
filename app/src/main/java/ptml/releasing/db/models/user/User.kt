package ptml.releasing.db.models.user

import com.google.gson.annotations.SerializedName

open class User(@SerializedName("username") var username: String?, @SerializedName("password") var password: String?)
