package ptml.releasing.cargo_search.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index (value = ["shipSideChassisNumbers"], unique = true)])
data class ShipSideChassisNumbers(@PrimaryKey(autoGenerate = true) val id: Int, val shipSideChassisNumbers: String?,
    val cargoType: String,val operationStep: Int,val terminal: Int,val shippingLine: String,val voyage: Int,val imei: String, val id_voyage: Int)