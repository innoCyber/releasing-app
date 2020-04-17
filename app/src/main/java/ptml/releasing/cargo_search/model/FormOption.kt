package ptml.releasing.cargo_search.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

data class FormOption(@SerializedName("selected") val selected: List<Int>?) : BaseModel() {
    override fun toString(): String {
        return "Option(selected=$selected) ${super.toString()}"
    }

    constructor(source: Parcel) : this(
        ArrayList<Int>().apply { source.readList(this, Int::class.java.classLoader) }
    ) {
        this.id = source.readInt()
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeList(selected)
        writeInt(id ?: 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<FormOption> = object : Parcelable.Creator<FormOption> {
            override fun createFromParcel(source: Parcel): FormOption = FormOption(source)
            override fun newArray(size: Int): Array<FormOption?> = arrayOfNulls(size)
        }
    }
}