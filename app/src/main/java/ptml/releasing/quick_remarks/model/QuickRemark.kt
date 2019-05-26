package ptml.releasing.quick_remarks.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel
import ptml.releasing.app.form.adapter.SelectModel

data class QuickRemark (@SerializedName("name") val name:String?):BaseModel(), SelectModel{
    override var checked: Boolean = false

    override fun text() = name ?: ""

    override fun id(): Int {
        return id ?: 0
    }

    var position = 0
    override fun position() = position
}