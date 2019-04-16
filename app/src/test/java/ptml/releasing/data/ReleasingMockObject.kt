package ptml.releasing.data

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import ptml.releasing.configuration.models.*
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.auth.model.User
import ptml.releasing.damages.model.Damage
import ptml.releasing.damages.model.DamageResponse
import java.net.SocketTimeoutException

const val IMEI = ""
const val VERIFY_DEVICE_FAILURE_MSG = "Device not authorized"
const val LOGIN_FAILURE_MSG = "User not authorized"

const val MOCK_TERMINAL_ID = 12
const val MOCK_CARGO_TYPE_ID = 133
const val MOCK_OPERATION_STEP_ID = 383
const val MOCK_TERMINAL_VALUE = "Terminal Value"
const val MOCK_CARGO_TYPE_VALUE = "Cargo  Value"
const val MOCK_OPERATION_STEP_VALUE = "Operation  Value"
const val MOCK_OPERATION_STEP_CATEGORY_ID = 43

const val SAVED_CONFIG_ERROR_MESSAGE = "Error occurred❗"

fun getVerifyDeviceSuccess() = BaseResponse("", true)
fun getVerifyDeviceFail() = BaseResponse(VERIFY_DEVICE_FAILURE_MSG, true)
fun getVerifyDeviceException() = SocketTimeoutException()

fun getUser() = User("ugo", "123456")
fun getLoginSuccess() = BaseResponse("", true)
fun getLoginFail() = BaseResponse(LOGIN_FAILURE_MSG, false)

fun getAdminConfigurationSuccess() =
    AdminConfigResponse(listOf<CargoType>(), listOf<OperationStep>(), listOf<Terminal>())

fun getAdminConfigurationFail() = AdminConfigResponse(null, null, null)

fun isConfiguredSuccess(): Deferred<Boolean> {
    return GlobalScope.async {
        true
    }
}

fun isConfiguredFail(): Deferred<Boolean> {
    return GlobalScope.async {
        false
    }
}

fun getSavedConfig(): Configuration {
    return Configuration(mockTerminal(), mockOperationStep(), mockCargoType(), true)
}

fun mockTerminal(): Terminal {
    val terminal = Terminal(2)
    terminal.id = MOCK_TERMINAL_ID
    terminal.value = MOCK_TERMINAL_VALUE
    return terminal
}

fun mockOperationStep(): OperationStep {
    val operationStep = OperationStep(MOCK_OPERATION_STEP_CATEGORY_ID)
    operationStep.value = MOCK_OPERATION_STEP_VALUE
    operationStep.id = MOCK_OPERATION_STEP_ID
    return operationStep
}

fun mockCargoType(): CargoType {
    val cargoType = CargoType()
    cargoType.value = MOCK_CARGO_TYPE_VALUE
    cargoType.id = MOCK_CARGO_TYPE_ID
    return cargoType
}

const val DAMAGE_SIZE = 20
const val DAMAGE_DESCRIPTION_PREFIX = "Description-"
const val DAMAGE_POSITION_MULTIPLIER = 3
const val DAMAGE_CONTAINER_MULTIPLIER = 2
fun downloadDamagesSuccess() = DamageResponse(getDamageList())

private fun getDamageList(): MutableList<Damage> {
    val list = mutableListOf<Damage>()
    for (i in 1..DAMAGE_SIZE) {
        list.add(getDamage(i))
    }

    return list
}

private fun getDamage(i: Int): Damage {
    val damage =
        Damage("$DAMAGE_DESCRIPTION_PREFIX$i", "${DAMAGE_POSITION_MULTIPLIER * i}", DAMAGE_CONTAINER_MULTIPLIER * i)
    damage.id = i
    return damage
}
