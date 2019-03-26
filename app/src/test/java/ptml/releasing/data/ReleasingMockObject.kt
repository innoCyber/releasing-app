package ptml.releasing.data

import ptml.releasing.admin_configuration.models.*
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.auth.model.User
import java.net.SocketTimeoutException


const val VERIFY_DEVICE_FAILURE_MSG = "Device not authorized"
const val LOGIN_FAILURE_MSG = "User not authorized"
fun getVerifyDeviceSuccess() = BaseResponse("", true)
fun getVerifyDeviceFail() = BaseResponse(VERIFY_DEVICE_FAILURE_MSG, false)
fun getVerifyDeviceException() = SocketTimeoutException()

fun getUser() = User("ugo", "123456")
fun getLoginSuccess() = BaseResponse("", true)
fun getLoginFail() = BaseResponse(LOGIN_FAILURE_MSG, false)

fun getAdminConfigurationSuccess() = AdminConfigResponse("", true, mutableListOf<CargoType>(), mutableListOf<OperationStep>(), mutableListOf<Terminal>())
fun getAdminConfigurationFail() = AdminConfigResponse("Error", false, null, null, null )
