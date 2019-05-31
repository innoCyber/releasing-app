package ptml.releasing.download_damages.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.AppResponse

data class DamageResponse(@SerializedName("data") val data: List<Damage>): AppResponse()