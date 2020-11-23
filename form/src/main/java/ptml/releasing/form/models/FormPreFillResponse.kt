package ptml.releasing.form.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FormPreFillResponse(
    @SerializedName("success") var isSuccess: Boolean = false,
    @SerializedName("message") var message: String? = null,
    @SerializedName("cargo_id") var cargoId: Int,
    @SerializedName("type_container") val typeContainer: Int,
    @SerializedName("barcode") var barcode: String?,
 //   @SerializedName("damages") var damage: List<FormDamage>,

    @SerializedName("values") val values: List<Value>?,
    @SerializedName("options") val options: List<Option>?
) : Parcelable