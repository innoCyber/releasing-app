package ptml.releasing.cargo_search.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadVoyageResponse (@SerializedName("success") val success: Boolean?,
                                   @SerializedName("message") val message: String?,
                                   @SerializedName("optionsPOD") val optionsPOD: List<PODOperationStep>): Parcelable

