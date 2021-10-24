package ptml.releasing.cargo_search.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PODOperationStep(
     @SerializedName("ID_POD") val ID_POD: Int,
     @SerializedName("ID_TYPE_VOYAGE") val ID_TYPE_VOYAGE: Int,
     @SerializedName("DESCRIPTION") val DESCRIPTION: String?
):Parcelable {
     constructor(parcel: Parcel) : this(
         parcel.readInt(),
         parcel.readInt(),
         parcel.readString()
     ) {
     }

     override fun writeToParcel(parcel: Parcel, flags: Int) {
         parcel.writeInt(ID_POD)
         parcel.writeInt(ID_TYPE_VOYAGE)
         parcel.writeString(DESCRIPTION)
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
        return " ID_POD: $ID_POD \n ID_TYPE_VOYAGE: $ID_TYPE_VOYAGE \n DESCRIPTION: $DESCRIPTION"
    }
 }