package ptml.releasing.cargo_search.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Header
import retrofit2.http.Query

@Parcelize
data class FindCargoItems(
    @SerializedName("cargo_type") var cargo_type: String?,
    @SerializedName("operation_step") var operation_step: Int?,
    @SerializedName("terminal") var terminal: Int?,
    @SerializedName("shipping_line") var shippingLine: String?,
    @SerializedName("voyage") var voyage: Int?,
    @SerializedName("IMEI") var imei: String?,
    @SerializedName("cargo_code") var cargo_code: String?,
    @SerializedName("id_voyage") var id_voyage: Int?
): Parcelable