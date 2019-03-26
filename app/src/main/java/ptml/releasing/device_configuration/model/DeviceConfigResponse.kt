package ptml.releasing.device_configuration.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.AppResponse
import ptml.releasing.app.base.BaseResponse

data class DeviceConfigResponse(@SerializedName("data") val data: List<BaseResponse>):AppResponse()