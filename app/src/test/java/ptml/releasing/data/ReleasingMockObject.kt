package ptml.releasing.data

import ptml.releasing.admin_configuration.models.*
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.auth.model.LoginResponse
import ptml.releasing.auth.model.User
import ptml.releasing.device_configuration.model.DeviceConfigResponse
import java.net.SocketTimeoutException


const val VERIFY_DEVICE_FAILURE_MSG = "Device not authorized"
const val LOGIN_FAILURE_MSG = "User not authorized"
fun getVerifyDeviceSuccess() = DeviceConfigResponse(mutableListOf(BaseResponse("", true)))
fun getVerifyDeviceFail() = DeviceConfigResponse(mutableListOf(BaseResponse(VERIFY_DEVICE_FAILURE_MSG, true)))
fun getVerifyDeviceException() = SocketTimeoutException()

fun getUser() = User("ugo", "123456")
fun getLoginSuccess() = LoginResponse(mutableListOf(BaseResponse("", true)))
fun getLoginFail() = LoginResponse(mutableListOf(BaseResponse(LOGIN_FAILURE_MSG, false)))

fun getAdminConfigurationSuccess() = AdminConfigResponse(mutableListOf<CargoType>(), mutableListOf<OperationStep>(), mutableListOf<Terminal>())
fun getAdminConfigurationFail() = AdminConfigResponse(null, null, null)
