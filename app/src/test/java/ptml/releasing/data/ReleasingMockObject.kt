package ptml.releasing.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ptml.releasing.configuration.models.*
import ptml.releasing.app.base.BaseResponse
import ptml.releasing.app.utils.Constants
import ptml.releasing.base.BaseTest
import ptml.releasing.cargo_info.model.FormSelection
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.model.Option
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.login.model.User
import ptml.releasing.download_damages.model.Damage
import ptml.releasing.download_damages.model.DamageResponse
import ptml.releasing.quick_remarks.model.QuickRemark
import ptml.releasing.quick_remarks.model.QuickRemarkResponse
import java.io.File
import java.net.SocketTimeoutException

const val VALID = true
const val INVALID = false
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
fun getVerifyDeviceFail() = BaseResponse(VERIFY_DEVICE_FAILURE_MSG, false)
fun getVerifyDeviceException() = SocketTimeoutException()

fun getUser() = User("ugo", "123456")
fun getLoginSuccess() = BaseResponse("", true)
fun getLoginFail() = BaseResponse(LOGIN_FAILURE_MSG, false)

fun getAdminConfigurationSuccess() =
    AdminConfigResponse(listOf<CargoType>(), listOf<OperationStep>(), listOf<Terminal>())

fun getAdminConfigurationFail() = AdminConfigResponse(null, null, null)

fun isConfiguredSuccess(): Boolean {
    return true
}

fun isConfiguredFail(): Boolean {
    return false
}

fun getSavedConfig(): Configuration {
    return Configuration(mockTerminal(), mockOperationStep(), mockCargoType(), true)
}

fun mockConfiguration(): ConfigureDeviceResponse {
    return ConfigureDeviceResponse(mutableListOf())
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
        Damage(
            "$DAMAGE_DESCRIPTION_PREFIX$i",
            "${DAMAGE_POSITION_MULTIPLIER * i}",
            DAMAGE_CONTAINER_MULTIPLIER * i
        )
    damage.id = i
    return damage
}

fun provideImei(): String {
    return ""
}

val findCargoResponse: FindCargoResponse by lazy {
    createFindCargoResponseSuccess()
}

val emptyValues = mapOf<Int?, Value>()

val emptyOptions = mapOf<Int?, Option>()


fun createFindCargoResponseSuccess(): FindCargoResponse {
    val json = getJson("findCargoResponseSucess.json")
    return Gson().fromJson(json, FindCargoResponse::class.java)
}

val emptyValuesList = listOf<Value>()
val emptyDamagesList = listOf<Damage>()
val emptyFormSelection = listOf<FormSelection>()

val quickRemarks: Map<Int, QuickRemark> by lazy {
    createQuickRemarks()
}

val quickRemarks6: Map<Int, QuickRemark> by lazy {
    createQuickRemarks6()
}

private fun createQuickRemarks(): Map<Int, QuickRemark> {
    val json = getJson("quickRemarks.json")
    val remarks = Gson().fromJson(json, QuickRemarkResponse::class.java)

    val map = mutableMapOf<Int, QuickRemark>()
    for (remark in remarks?.data ?: mutableListOf()) {
        map[remark.id ?: continue] = remark
    }
    return map
}

private fun createQuickRemarks6(): Map<Int, QuickRemark> {
    val json = getJson("quickRemarks6.json")
    val remarks = Gson().fromJson(json, QuickRemarkResponse::class.java)

    val map = mutableMapOf<Int, QuickRemark>()
    for (remark in remarks?.data ?: mutableListOf()) {
        map[remark.id ?: continue] = remark
    }
    return map
}

const val TEXT_BOX_ID = 2
const val TEXT_BOX_TITLE = "Enter Grid"
const val TEXT_BOX_VALIDATION = "alphanumeric"
fun getTextBoxData(): ConfigureDeviceData {
    return ConfigureDeviceData(
        TEXT_BOX_ID,
        Constants.TEXT_BOX,
        TEXT_BOX_TITLE,
        true,
        false,
        mutableListOf(),
        TEXT_BOX_VALIDATION
    )
}


const val BUTTON_ID = 33
const val BUTTON_NUMBER = 43
const val BUTTON_TITLE = "Damages"
fun buttonData(): ConfigureDeviceData {
    return ConfigureDeviceData(
        BUTTON_ID,
        Constants.DAMAGES,
        BUTTON_TITLE,
        true,
        false,
        mutableListOf(),
        ""
    )
}


const val SINGLE_SELECT_ID = 33
const val SINGLE_SELECT_TITLE = "Damages"
fun getSingleDataWithOptions(): ConfigureDeviceData {
    return ConfigureDeviceData(
        SINGLE_SELECT_ID,
        Constants.SINGLE_SELECT,
        SINGLE_SELECT_TITLE,
        true,
        false,
        singleSelectOptions,
        ""
    )
}

fun getSingleDataNoOptions(): ConfigureDeviceData {
    return ConfigureDeviceData(
        SINGLE_SELECT_ID,
        Constants.SINGLE_SELECT,
        SINGLE_SELECT_TITLE,
        true,
        false,
        mutableListOf(),
        ""
    )
}

fun getSingleDataOneOptions(): ConfigureDeviceData {
    return ConfigureDeviceData(
        SINGLE_SELECT_ID,
        Constants.SINGLE_SELECT,
        SINGLE_SELECT_TITLE,
        true,
        false,
        listOf(optionsSample),
        ""
    )
}

const val SELECTED_POSITION = 0

val singleSelectOptions by lazy {
    provideSingleSelectOptions()
}

const val OPTION_NAME = "Option name"
const val OPTION_ID = 32
val optionsSample by lazy {
    val options = Options(OPTION_NAME, true)
    options.id = OPTION_ID
    options
}

fun provideSingleSelectOptions(): List<Options> {
    val json = getJson("singleSelectOptions.json")
    val type = object : TypeToken<List<Options>>() {}
    return Gson().fromJson(json, type.type)
}


val quickRemarkData by lazy {
    provideQuickRemarkData()
}

const val QUICK_REMARK_NAME = "Option name"
const val QUICK_REMARK_ID = 32
fun provideQuickRemarkData(): ConfigureDeviceData {
    return ConfigureDeviceData(
        QUICK_REMARK_ID,
        Constants.QUICK_REMARKS,
        QUICK_REMARK_NAME,
        true,
        false,
        mutableListOf(),
        ""
    )
}


const val MULTI_SELECT_NAME = "Multiselect name"
const val MULTI_SELECT_ID = 333
fun provideMultiSelectData(): ConfigureDeviceData {
    return ConfigureDeviceData(
        MULTI_SELECT_ID,
        Constants.MULTI_SELECT,
        MULTI_SELECT_NAME,
        true,
        false,
        provideMultiSelectOptions(),
        ""
    )
}


fun provideMultiSelectDataWithOneOption(): ConfigureDeviceData {
    return ConfigureDeviceData(
        MULTI_SELECT_ID,
        Constants.MULTI_SELECT,
        MULTI_SELECT_NAME,
        true,
        false,
        listOf(optionsSample),
        ""
    )
}

fun provideMultiSelectDataNoOption(): ConfigureDeviceData {
    return ConfigureDeviceData(
        MULTI_SELECT_ID,
        Constants.MULTI_SELECT,
        MULTI_SELECT_NAME,
        true,
        false,
        listOf(),
        ""
    )
}

private fun provideMultiSelectOptions(): List<Options> {
    val json = getJson("multiSelectOptions.json")
    val type = object : TypeToken<List<Options>>() {}
    return Gson().fromJson(json, type.type)
}


val multiSelectItemsList by lazy {
    provideMultiSelectItemsList()
}

val multiSelectItemsListAtLeast6 by lazy {
    provideMultiSelectItemsList6()
}

fun provideMultiSelectItemsList(): List<Int> {
    return quickRemarks.map { it.key }.toList()
}

fun provideMultiSelectItemsList6(): List<Int> {
    return quickRemarks6.map { it.key }.toList()
}


const val CHECK_BOX_NAME = "Check name"
const val CHECK_BOX_ID = 354
fun provideCheckBoxData(): ConfigureDeviceData {
    return ConfigureDeviceData(
        CHECK_BOX_ID,
        Constants.CHECK_BOX,
        CHECK_BOX_NAME,
        true,
        false,
        listOf(),
        ""
    )
}


val configureDeviceData: List<ConfigureDeviceData> by lazy {
    provideConfigureDeviceData()
}


val configureDeviceDataNonRequired: List<ConfigureDeviceData> by lazy {
    provideConfigureDeviceDataNonRequired()
}

val configureDeviceDataSomeRequired: List<ConfigureDeviceData> by lazy {
    provideConfigureDeviceDataSomeRequired()
}

val configureDeviceDataWithInvalidFormType: List<ConfigureDeviceData> by lazy {
    configureDeviceDataWithInvalidFormType()
}


private fun configureDeviceDataWithInvalidFormType(): MutableList<ConfigureDeviceData> {
    val list = configureDeviceData.toMutableList()
    val configureDeviceData = ConfigureDeviceData(
        2,
        Constants.UNKNOWN,
        "",
        true,
        false,
        listOf(),
        ""

    )

    list.add(configureDeviceData)
    return list
}

private fun provideConfigureDeviceData(): List<ConfigureDeviceData> {
    val json = getJson("setConfigDeviceSuccess.json")

    return Gson().fromJson(json, ConfigureDeviceResponse::class.java).data
}

private fun provideConfigureDeviceDataNonRequired(): List<ConfigureDeviceData> {
    val json = getJson("setConfigDeviceSuccessNonRequired.json")
    return Gson().fromJson(json, ConfigureDeviceResponse::class.java).data
}

private fun provideConfigureDeviceDataSomeRequired(): List<ConfigureDeviceData> {
    val json = getJson("setConfigDeviceSuccessSomeRequired.json")
    return Gson().fromJson(json, ConfigureDeviceResponse::class.java).data
}

private fun getJson(path: String): String {
    val uri = BaseTest::class.java.classLoader?.getResource(path)
    val file = File(uri?.path ?: return "")
    return String(file.readBytes())
}






