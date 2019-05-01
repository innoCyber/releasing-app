package ptml.releasing.cargo_search.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseResponse

data class FindCargoResponse(
    @SerializedName("cargo_id") var cargoId: Int,
    @SerializedName("values") val values: List<Value>,
    @SerializedName("options") val options: List<Option>
) : BaseResponse(), Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        ArrayList<Value>().apply { source.readList(this, Value::class.java.classLoader) },
        ArrayList<Option>().apply { source.readList(this, Option::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(cargoId)
        writeList(values)
        writeList(options)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FindCargoResponse> = object : Parcelable.Creator<FindCargoResponse> {
            override fun createFromParcel(source: Parcel): FindCargoResponse = FindCargoResponse(source)
            override fun newArray(size: Int): Array<FindCargoResponse?> = arrayOfNulls(size)
        }
    }
}