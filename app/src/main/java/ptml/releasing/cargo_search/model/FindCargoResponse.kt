package ptml.releasing.cargo_search.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FindCargoResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("success") val isSuccess: Boolean = false,
    @SerializedName("cargo_id") var cargoId: Int,
    @SerializedName("type_container") val typeContainer: Int,
    @SerializedName("barcode") var barcode: String?,
    @SerializedName("mrkno") var mrkNumber: String?,
    @SerializedName("GrimandiContainer") var grimaldiContainer: String?,
    @SerializedName("values") val values: List<FormValue>?,
    @SerializedName("options") val options: List<FormOption>?
) : Parcelable