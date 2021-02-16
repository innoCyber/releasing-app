package ptml.releasing.form.models

import androidx.annotation.StringDef
import com.google.gson.annotations.SerializedName
import ptml.releasing.form.models.base.BaseModel

data class FormDamage(
    @SerializedName("number") val number: Int,
    @SerializedName("type") val type: String,
    @SerializedName("remarks") val remarks: String,
    @SerializedName("location") val location: String,
    @SerializedName("size") val size: String,
    @SerializedName("damage") val damages: List<Damage>

) : BaseModel(){
    override fun toString(): String {
        return "DamageData(position=$number, type='$type', title='$remarks', required=$location, size='$size') ${super.toString()}"
    }

}

@StringDef(SMALL, LARGE)
annotation class FormDamageSize

const val SMALL = "Small"
const val LARGE = "Large"