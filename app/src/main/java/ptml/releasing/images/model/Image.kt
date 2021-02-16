package ptml.releasing.images.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.webkit.URLUtil
import com.google.gson.annotations.SerializedName
import java.io.File

/**
Created by kryptkode on 8/5/2019
 */

data class Image constructor(
    @SerializedName("imageUri") var imageUri: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("uploaded") var uploaded: Boolean = false
) :
    Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString()
    )

    fun isFile(): Boolean {
        return URLUtil.isFileUrl(imageUri)
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(imageUri)
        writeString(name)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Image> = object : Parcelable.Creator<Image> {
            override fun createFromParcel(source: Parcel): Image = Image(source)
            override fun newArray(size: Int): Array<Image?> = arrayOfNulls(size)
        }
    }
}

