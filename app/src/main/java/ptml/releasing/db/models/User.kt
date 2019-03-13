package ptml.releasing.db.models

import com.google.gson.annotations.SerializedName

class User(username: String, password: String) {

    @SerializedName("username")
    var username: String? = username

    @SerializedName("password")
    var password: String? = password

}
