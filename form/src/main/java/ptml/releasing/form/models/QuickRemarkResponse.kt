package ptml.releasing.form.models

import com.google.gson.annotations.SerializedName
import ptml.releasing.form.models.base.BaseResponse


data class QuickRemarkResponse(@SerializedName("data") val data: List<QuickRemark>) :
    BaseResponse()