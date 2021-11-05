package ptml.releasing.cargo_search.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import ptml.releasing.app.data.remote.result.VoyageRemote

@Parcelize
data class PODOperationStep(
     @SerializedName("id") val id: Int?,
     @SerializedName("id_type_voyage") val id_type_voyage: Int?,
     @SerializedName("name") val name: String,
     @SerializedName("id_voyage") val id_voyage: Int?
): Parcelable
