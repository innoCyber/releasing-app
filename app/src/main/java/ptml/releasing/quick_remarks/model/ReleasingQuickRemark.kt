package ptml.releasing.quick_remarks.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

data class ReleasingQuickRemark(@SerializedName("description") val name: String?) : BaseModel()