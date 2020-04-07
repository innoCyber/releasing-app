package ptml.releasing.quick_remarks.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseResponse


data class QuickRemarkResponse(@SerializedName("data") val data: List<ReleasingQuickRemark>) :
    BaseResponse()