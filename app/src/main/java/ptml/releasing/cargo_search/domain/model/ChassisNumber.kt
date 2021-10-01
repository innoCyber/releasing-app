package ptml.releasing.cargo_search.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChassisNumber(@PrimaryKey(autoGenerate = true) val id: Int, val chasisNumber: String?){
}