package ptml.releasing.cargo_search.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PODOperationStep(
     @SerializedName("name") val name: String?
):Parcelable {
     constructor(parcel: Parcel) : this(
         parcel.readString()
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
         parcel.writeString(name)
     }

     override fun describeContents(): Int {
         return 0
     }

     companion object CREATOR : Parcelable.Creator<PODOperationStep> {
         override fun createFromParcel(parcel: Parcel): PODOperationStep {
             return PODOperationStep(parcel)
         }

         override fun newArray(size: Int): Array<PODOperationStep?> {
             return arrayOfNulls(size)
         }
     }

    override fun toString(): String {
        return name?: ""
    }
 }