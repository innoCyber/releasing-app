package ptml.releasing.cargo_search.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["chasisNumber"], unique = true)])
data class ChassisNumber(@PrimaryKey(autoGenerate = true) val id: Int, val chasisNumber: String?){
}