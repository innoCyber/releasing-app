package ptml.releasing.app.data.domain.model.login

import com.google.gson.annotations.SerializedName

/**
 * Created by kryptkode on 10/27/2019.
 */
data class AdminOptionsEntity(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("cargo_type") val cargoType: List<CargoType>?,
    @SerializedName("operation_step") val operationSteps: List<OperationStep>?,
    @SerializedName("terminal") val terminals: List<Terminal>?
)