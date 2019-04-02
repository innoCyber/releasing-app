package ptml.releasing.data

import ptml.releasing.admin_configuration.models.*
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.auth.model.LoginResponse
import ptml.releasing.auth.model.User
import ptml.releasing.damages.model.Damage
import ptml.releasing.damages.model.DamageResponse
import ptml.releasing.device_configuration.model.DeviceConfigResponse
import java.net.SocketTimeoutException

const val IMEI = ""
const val VERIFY_DEVICE_FAILURE_MSG = "Device not authorized"
const val LOGIN_FAILURE_MSG = "User not authorized"
fun getVerifyDeviceSuccess() = DeviceConfigResponse(listOf(BaseResponse("", true)))
fun getVerifyDeviceFail() = DeviceConfigResponse(listOf(BaseResponse(VERIFY_DEVICE_FAILURE_MSG, true)))
fun getVerifyDeviceException() = SocketTimeoutException()

fun getUser() = User("ugo", "123456")
fun getLoginSuccess() = LoginResponse(listOf(BaseResponse("", true)))
fun getLoginFail() = LoginResponse(listOf(BaseResponse(LOGIN_FAILURE_MSG, false)))

fun getAdminConfigurationSuccess() = AdminConfigResponse(listOf<CargoType>(), listOf<OperationStep>(), listOf<Terminal>())
fun getAdminConfigurationFail() = AdminConfigResponse(null, null, null)

const val DAMAGE_SIZE = 20
const val DAMAGE_DESCRIPTION_PREFIX = "Description-"
const val DAMAGE_POSITION_MULTIPLIER = 3
const val DAMAGE_CONTAINER_MULTIPLIER = 2
fun downloadDamagesSuccess() = DamageResponse(getDamageList())

private fun getDamageList(): MutableList<Damage> {
    val list = mutableListOf<Damage>()
    for (i in 1..DAMAGE_SIZE){
        list.add(getDamage(i))
    }

    return list
}
private fun getDamage(i:Int) = Damage(i, "$DAMAGE_DESCRIPTION_PREFIX$i", "${DAMAGE_POSITION_MULTIPLIER*i}", DAMAGE_CONTAINER_MULTIPLIER*i)