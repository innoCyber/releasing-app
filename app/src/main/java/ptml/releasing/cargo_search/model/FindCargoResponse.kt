package ptml.releasing.cargo_search.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseResponse

data class FindCargoResponse(
    @SerializedName("cargo_id") var cargoId: Int,
    @SerializedName("type_container") val typeContainer: Int,
    @SerializedName("barcode") var barcode: String?,
    @SerializedName("values") val values: List<FormValue>?,
    @SerializedName("options") val options: List<FormOption>?
) : BaseResponse(), Parcelable {

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt(),
        source.readString(),
        source.createTypedArrayList(FormValue.CREATOR),
        source.createTypedArrayList(FormOption.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(cargoId)
        writeInt(typeContainer)
        writeString(barcode)
        writeTypedList(values)
        writeTypedList(options)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FindCargoResponse> =
            object : Parcelable.Creator<FindCargoResponse> {
                override fun createFromParcel(source: Parcel): FindCargoResponse =
                    FindCargoResponse(source)

                override fun newArray(size: Int): Array<FindCargoResponse?> = arrayOfNulls(size)
            }
    }
}