package ptml.releasing.configuration.models

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.AppResponse
import ptml.releasing.app.base.BaseModel
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.form.adapter.SelectModel


open class BaseConfig : BaseModel() {

    @SerializedName("value")
    var value: String? = null


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseConfig) return false
        if (!super.equals(other)) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "BaseConfig(value=$value) ${super.toString()}"
    }


}

class CargoType : BaseConfig() {
    companion object {
        const val VEHICLE = "vehicle"
        const val CONTAINER = "container"
        const val GENERAL = "general"
    }

}

data class Terminal(
        @SerializedName("cargo_type")
        val categoryTypeId: Int
) : BaseConfig() {


    override fun toString(): String {
        return "Terminal(cargo_type=$categoryTypeId) Super=(${super.toString()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Terminal) return false
        if (!super.equals(other)) return false

        if (categoryTypeId != other.categoryTypeId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + categoryTypeId
        return result
    }


}


data class OperationStep(
        @SerializedName("cargo_type")
        val cargo_type: Int
) : BaseConfig() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OperationStep) return false
        if (!super.equals(other)) return false

        if (cargo_type != other.cargo_type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + cargo_type
        return result
    }

    override fun toString(): String {
        return "OperationStep(cargo_type=$cargo_type) ${super.toString()}"
    }


}


data class AdminConfigResponse(
        @SerializedName("cargo_type")
        val cargoTypeList: List<CargoType>?,
        @SerializedName("operation_step")
        val operationStepList: List<OperationStep>?,
        @SerializedName("terminal")
        val terminalList: List<Terminal>?
) : AppResponse() {


    constructor() : this(mutableListOf(), mutableListOf(), mutableListOf())


    /**
     * Used to check for equality in instances of this class
     * */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AdminConfigResponse) return false

        if (cargoTypeList != other.cargoTypeList) return false
        if (operationStepList != other.operationStepList) return false
        if (terminalList != other.terminalList) return false

        return true
    }

    /**
     * Used in conjuction with @link AdminConfigResponse#equals to  check for equality in instances of this class
     * */
    override fun hashCode(): Int {
        var result = cargoTypeList.hashCode()
        result = 31 * result + operationStepList.hashCode()
        result = 31 * result + terminalList.hashCode()
        return result
    }


}

data class Configuration(
        @SerializedName("terminal") val terminal: Terminal,
        @SerializedName("operationStep") val operationStep: OperationStep,
        @SerializedName("cargoType") val cargoType: CargoType,
        @SerializedName("cameraEnabled") val cameraEnabled: Boolean
) : AppResponse() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Configuration) return false

        if (terminal != other.terminal) return false
        if (operationStep != other.operationStep) return false
        if (cargoType != other.cargoType) return false
        if (cameraEnabled != other.cameraEnabled) return false

        return true
    }

    override fun hashCode(): Int {
        var result = terminal.hashCode()
        result = 31 * result + operationStep.hashCode()
        result = 31 * result + cargoType.hashCode()
        result = 31 * result + cameraEnabled.hashCode()
        return result
    }

    override fun toString(): String {
        return "Configuration(terminal=$terminal, operationStep=$operationStep, cargoType=$cargoType, cameraEnabled=$cameraEnabled)"
    }


}

data class ConfigureDeviceParams(
        @SerializedName("cargo_type") val cargoTypeId: Int,
        @SerializedName("operation_step") val operationStepId: Int,
        @SerializedName("terminal") val terminal: Int,
        @SerializedName("imei") val imei: String
)

data class ConfigureDeviceResponse(@SerializedName("data") val data: List<ConfigureDeviceData>) : BaseResponse() {
    override fun toString(): String {
        return "ConfigureDeviceResponse(data=$data)"
    }
}

data class ConfigureDeviceData(
        @SerializedName("order_id") val position: Int = 0,
        @SerializedName("type") val type: String,
        @SerializedName("title") val title: String,
        @SerializedName("is_required") val required: Boolean,
        @SerializedName("is_editable") val editable: Boolean,
        @SerializedName("options") val options: List<Options>,
        @SerializedName("data_validation") val dataValidation: String
) : BaseModel() {
    override fun toString(): String {
        return "ConfigureDeviceData(position=$position, type='$type', title='$title', required=$required, editable=$editable, options=$options, dataValidation='$dataValidation') ${super.toString()}"
    }
}

data class Options(
        @SerializedName("name") val name: String,
        @SerializedName("is_selected") val isSelected: Boolean
) : BaseModel(), SelectModel {

    override var checked: Boolean = isSelected

    override fun getText() = name

}
