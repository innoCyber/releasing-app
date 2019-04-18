package ptml.releasing.cargo_search.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

data class Option(@SerializedName("selected") val selected: List<Int>?) : BaseModel() {
    constructor(source: Parcel) : this(
        ArrayList<Int>().apply { source.readList(this, Int::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeList(selected)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Option> = object : Parcelable.Creator<Option> {
            override fun createFromParcel(source: Parcel): Option = Option(source)
            override fun newArray(size: Int): Array<Option?> = arrayOfNulls(size)
        }
    }
}