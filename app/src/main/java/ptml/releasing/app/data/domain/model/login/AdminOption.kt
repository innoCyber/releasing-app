package ptml.releasing.app.data.domain.model.login

import com.google.gson.annotations.SerializedName

/**
 * Created by kryptkode on 10/27/2019.
 */
open class AdminOption(
    @SerializedName("id") val id: Int?,
    @SerializedName("value") val value: String?
) {
    override fun toString(): String {
        return "AdminOption(id=$id, value=$value)"
    }
}

class CargoType(id: Int?, value: String?) : AdminOption(id, value) {
    override fun toString(): String {
        return "CargoType(id=$id, value=$value)"
    }
}

class OperationStep(
    @SerializedName("cargo_type") val cargoTypeId: Int?,
    id: Int?, value: String?
) : AdminOption(id, value) {
    override fun toString(): String {
        return "OperationStep(id=$id, value=$value)"
    }
}

class Terminal(
    @SerializedName("cargo_type") val cargoTypeId: Int?,
    id: Int?, value: String?
) : AdminOption(id, value) {
    override fun toString(): String {
        return "Terminal(id=$id, value=$value)"
    }
}