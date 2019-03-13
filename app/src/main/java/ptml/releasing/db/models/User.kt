package ptml.releasing.db.models

import com.google.gson.annotations.SerializedName

class User(@SerializedName("username") var username: String?, @SerializedName("password") var password: String?) {

}
