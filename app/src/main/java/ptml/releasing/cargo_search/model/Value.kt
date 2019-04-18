package ptml.releasing.cargo_search.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

data class Value(@SerializedName("value") val value: String?) : BaseModel() {
    constructor(source: Parcel) : this(
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(value)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Value> = object : Parcelable.Creator<Value> {
            override fun createFromParcel(source: Parcel): Value = Value(source)
            override fun newArray(size: Int): Array<Value?> = arrayOfNulls(size)
        }
    }
}