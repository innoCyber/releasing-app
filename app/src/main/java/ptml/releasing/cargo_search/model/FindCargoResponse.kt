package ptml.releasing.cargo_search.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseResponse

data class FindCargoResponse(
    @SerializedName("values") val values: List<Value>,
    @SerializedName("options") val options: List<Option>
) : BaseResponse(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Value.CREATOR),
        parcel.createTypedArrayList(Option.CREATOR)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(values)
        parcel.writeTypedList(options)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FindCargoResponse> {
        override fun createFromParcel(parcel: Parcel): FindCargoResponse {
            return FindCargoResponse(parcel)
        }

        override fun newArray(size: Int): Array<FindCargoResponse?> {
            return arrayOfNulls(size)
        }
    }

}