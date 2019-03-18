package ptml.releasing.admin_configuration.models

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel
import ptml.releasing.app.base.BaseResponse
import java.lang.reflect.Type

open class BaseConfig : BaseModel() {

    @SerializedName("value")
    var value: String? = null

    override fun toJson(): JsonObject {
        val obj = super.toJson()
        obj.addProperty("value", value)
        return obj
    }

}

class CargoType : BaseConfig()

class CargoTypeSerializer : JsonSerializer<CargoType> {
    override fun serialize(src: CargoType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return src?.toJson() ?: JsonObject()
    }

}

class CargoTypeDeserializer : JsonDeserializer<CargoType> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): CargoType {
        val cargoType = CargoType()
        cargoType.id = json?.asJsonObject?.get("id")?.asInt ?: 0
        cargoType.value = json?.asJsonObject?.get("value")?.asString
        return cargoType;
    }
}

class Terminal : BaseConfig()

class TerminalSerializer : JsonSerializer<Terminal> {
    override fun serialize(src: Terminal?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return src?.toJson() ?: JsonObject()
    }

}

class TerminalDeserializer : JsonDeserializer<Terminal> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Terminal {
        val cargoType = Terminal()
        cargoType.id = json?.asJsonObject?.get("id")?.asInt ?: 0
        cargoType.value = json?.asJsonObject?.get("value")?.asString
        return cargoType;
    }
}

class OperationStep : BaseConfig() {
    @SerializedName("cargo_type_id")
    var categoryTypeId: Int = 0

    override fun toJson(): JsonObject {
        val obj = super.toJson()
        obj.addProperty("cargo_type_id", categoryTypeId)
        return obj
    }
}

class OperationStepSerializer : JsonSerializer<OperationStep> {
    override fun serialize(src: OperationStep?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return src?.toJson() ?: JsonObject()
    }

}

class OperationStepDeserializer : JsonDeserializer<OperationStep> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): OperationStep {
        val operationStep = OperationStep()
        operationStep.id = json?.asJsonObject?.get("id")?.asInt ?: 0
        operationStep.value = json?.asJsonObject?.get("value")?.asString
        operationStep.categoryTypeId = json?.asJsonObject?.get("cargo_type_id")?.asInt ?: 0
        return operationStep;
    }
}


class ConfigurationResponse : BaseResponse {


    @SerializedName("cargo_type")
    var cargoTypeList: List<CargoType>? = null

    @SerializedName("operation_step")
    var operationStepList: List<OperationStep>? = null


    @SerializedName("terminal")
    var terminalList: List<Terminal>? = null


    constructor(
        message: String,
        success: Boolean,
        cargoTypeList: List<CargoType>,
        operationStepList: List<OperationStep>,
        terminalList: List<Terminal>
    ) : super(message, success) {
        this.cargoTypeList = cargoTypeList
        this.operationStepList = operationStepList
        this.terminalList = terminalList
    }

    constructor() {}


}
