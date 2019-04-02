package ptml.releasing.auth.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.AppResponse
import ptml.releasing.app.base.BaseResponse

data class LoginResponse(@SerializedName("data") val data: List<BaseResponse>): AppResponse()