package ptml.releasing.cargo_info.model

import androidx.annotation.StringDef
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

data class FormDamage(
    @SerializedName("number") val number:Int,
    @SerializedName("type") val type:Int,
    @SerializedName("remarks") val remarks:String,
    @SerializedName("location") val location:String,
    @SerializedName("size") val size:String
):BaseModel()

@StringDef(SMALL, LARGE)
annotation class FormDamageSize

const val SMALL = "Small"
const val LARGE = "Large"