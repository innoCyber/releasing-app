package ptml.releasing.form.models.base

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


@Suppress("UNUSED_PARAMETER")
open class BaseModel() : Parcelable {
    @SerializedName("id")
    open var id: Int? = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseModel) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id ?: 0
    }

    override fun toString(): String {
        return "BaseModel(id=$id)"
    }

    constructor(source: Parcel) : this(
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BaseModel> = object : Parcelable.Creator<BaseModel> {
            override fun createFromParcel(source: Parcel): BaseModel = BaseModel(source)
            override fun newArray(size: Int): Array<BaseModel?> = arrayOfNulls(size)
        }
    }
}
