package ptml.releasing.app.data.remote.result

import com.google.gson.annotations.SerializedName
import ptml.releasing.cargo_search.model.PODOperationStep
import ptml.releasing.configuration.models.ReleasingOptions

/**
 * Created by kryptkode on 4/9/2020.
 */
data class VoyageResult(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("operation_step") val data: List<VoyageRemote>?,
    @SerializedName("message") val message: String?,
    @SerializedName("optionsPOD") val optionsPOD: List<PODOperationStep>
)