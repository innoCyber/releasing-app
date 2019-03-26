package ptml.releasing.damages.model

import com.google.gson.annotations.SerializedName

data class DamageResponse(@SerializedName("data") val data: List<Damage>)