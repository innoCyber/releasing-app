package ptml.releasing.images.model

import android.os.Parcel
import android.os.Parcelable
import java.io.File

/**
Created by kryptkode on 8/5/2019
 */

data class Image(val file: File) : Parcelable {
    constructor(source: Parcel) : this(
        source.readSerializable() as File
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeSerializable(file)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Image> = object : Parcelable.Creator<Image> {
            override fun createFromParcel(source: Parcel): Image = Image(source)
            override fun newArray(size: Int): Array<Image?> = arrayOfNulls(size)
        }
    }
}

