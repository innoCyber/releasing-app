package ptml.releasing.db.models.base

import com.google.gson.annotations.SerializedName

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class BaseModel {
    @SerializedName("id")
    @PrimaryKey
    var id: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseModel) return false

        val baseModel = other as BaseModel?

        return id == baseModel!!.id
    }

    override fun hashCode(): Int {
        return id
    }
}
