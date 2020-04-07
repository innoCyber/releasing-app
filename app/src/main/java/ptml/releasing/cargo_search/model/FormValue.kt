package ptml.releasing.cargo_search.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

data class FormValue(@SerializedName("value") val value: String?) : BaseModel(), Parcelable {
    override fun toString(): String {
        return "Value(value=$value) ${super.toString()}"
    }

    constructor(source: Parcel) : this(
        source.readString()
    ){
        this.id = source.readInt()
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(value)
        writeInt(id ?: 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FormValue> = object : Parcelable.Creator<FormValue> {
            override fun createFromParcel(source: Parcel): FormValue = FormValue(source)
            override fun newArray(size: Int): Array<FormValue?> = arrayOfNulls(size)
        }
    }
}