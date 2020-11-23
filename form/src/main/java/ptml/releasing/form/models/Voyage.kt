package ptml.releasing.form.models

import com.google.gson.annotations.SerializedName
import ptml.releasing.form.adapter.SelectModel

/**
 * Created by kryptkode on 4/8/2020.
 */
data class Voyage(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val vesselName: String
) : SelectModel {
    override var checked: Boolean = false

    override fun text() = vesselName

    override fun id(): Int {
        return id
    }

    var position = 0
    override fun position() = position
}