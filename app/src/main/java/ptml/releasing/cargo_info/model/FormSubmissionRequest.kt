package ptml.releasing.cargo_info.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.cargo_search.model.FormValue

data class FormSubmissionRequest(
    @SerializedName("values") val values: List<FormValue>?,
    @SerializedName("selections") val selections: List<ReleasingFormSelection>?,
    @SerializedName("damages") val damages: List<FormDamage>?,
    @SerializedName("cargo_type") val cargoTypeId: Int?,
    @SerializedName("operation_step") val  operationStepId: Int?,
    @SerializedName("terminal") val terminal: Int?,
    @SerializedName("operator") val operator: String?,
    @SerializedName("cargo_code") val cargoCode: String?,
    @SerializedName("cargo_id") val cargoId: Int?,
    @SerializedName("imei") val imei: String?
)