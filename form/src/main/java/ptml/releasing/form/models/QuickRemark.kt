package ptml.releasing.form.models

import com.google.gson.annotations.SerializedName
import ptml.releasing.form.adapter.SelectModel
import ptml.releasing.form.models.base.BaseModel

data class QuickRemark(@SerializedName("description") val name: String?) : BaseModel(),
    SelectModel {
    override var checked: Boolean = false

    override fun text() = name ?: "e"

    override fun id(): Int {
        return id ?: 0
    }

    var position = 0
    override fun position() = position
}