package ptml.releasing.form.models

import com.google.gson.annotations.SerializedName

data class FormSubmissionRequest(
    @SerializedName("values") val values: List<Value>?,
    @SerializedName("selections") val selections: List<FormSelection>?,
    @SerializedName("damages") val damages: List<FormDamage>?,
    @SerializedName("cargo_type") val cargoTypeId: Int?,
    @SerializedName("operation_step") val operationStepId: Int?,
    @SerializedName("terminal") val terminal: Int?,
    @SerializedName("operator") val operator: String?,
    @SerializedName("cargo_code") val cargoCode: String?,
    @SerializedName("cargo_id") val cargoId: Int?,
    @SerializedName("imei") val imei: String?
)