package ptml.releasing.form.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import ptml.releasing.form.models.base.BaseModel

data class Value(@SerializedName("value") val value: String?) : BaseModel(), Parcelable {
    override fun toString(): String {
        return "Value(value=$value) ${super.toString()}"
    }

    constructor(source: Parcel) : this(
        source.readString()
    ) {
        this.id = source.readInt()
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(value)
        writeInt(id ?: 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Value> = object : Parcelable.Creator<Value> {
            override fun createFromParcel(source: Parcel): Value = Value(source)
            override fun newArray(size: Int): Array<Value?> = arrayOfNulls(size)
        }
    }
}