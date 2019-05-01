package ptml.releasing.cargo_info.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.cargo_search.model.Value

data class FormSubmissionRequest(
    @SerializedName("values") val values: List<Value>?,
    @SerializedName("selections") val selections: List<FormSelection>?,
    @SerializedName("damages") val damages: List<FormDamage>?
)