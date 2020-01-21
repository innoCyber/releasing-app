package ptml.releasing.app.data.remote.result

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.data.domain.model.login.CargoType
import ptml.releasing.app.data.domain.model.login.OperationStep
import ptml.releasing.app.data.domain.model.login.Terminal

/**
 * Created by kryptkode on 10/27/2019.
 */
data class AdminOptionsResult(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("cargo_type") val cargoTypes: List<CargoType>?,
    @SerializedName("operation_step")
    val operationSteps: List<OperationStep>?,
    @SerializedName("terminal")
    val terminals: List<Terminal>?,
    @SerializedName("message") val message: String?
)