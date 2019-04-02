package ptml.releasing.admin_configuration.models

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.AppResponse
import ptml.releasing.app.base.BaseModel
import ptml.releasing.app.base.BaseResponse
import java.lang.reflect.Type

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
        return "BaseConfig(value=$value)"
    }


}

class CargoType : BaseConfig()

class Terminal : BaseConfig()


data class OperationStep(@SerializedName("cargo_id")
                         val categoryTypeId: Int) : BaseConfig(){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OperationStep) return false
        if (!super.equals(other)) return false

        if (categoryTypeId != other.categoryTypeId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + categoryTypeId
        return result
    }

    override fun toString(): String {
        return "OperationStep(categoryTypeId=$categoryTypeId)"
    }


}


class AdminConfigResponse : AppResponse {


    @SerializedName("cargo_type")
    var cargoTypeList: List<CargoType>? = null

    @SerializedName("operation_step")
    var operationStepList: List<OperationStep>? = null


    @SerializedName("terminal")
    var terminalList: List<Terminal>? = null


    constructor(
        cargoTypeList: List<CargoType>?,
        operationStepList: List<OperationStep>?,
        terminalList: List<Terminal>?
    ) {
        this.cargoTypeList = cargoTypeList
        this.operationStepList = operationStepList
        this.terminalList = terminalList
    }

    constructor()


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
        var result = cargoTypeList?.hashCode() ?: 0
        result = 31 * result + (operationStepList?.hashCode() ?: 0)
        result = 31 * result + (terminalList?.hashCode() ?: 0)
        return result
    }


}

data class Configuration(
    @SerializedName("terminal") val terminal: Terminal,
    @SerializedName("operationStep") val operationStep: OperationStep,
    @SerializedName("cargoType") val cargoType: CargoType,
    @SerializedName("cameraEnabled") val cameraEnabled: Boolean
):AppResponse(){
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
