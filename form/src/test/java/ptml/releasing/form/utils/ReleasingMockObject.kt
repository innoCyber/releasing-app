package ptml.releasing.form.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ptml.releasing.form.base.BaseTest
import ptml.releasing.form.models.*
import ptml.releasing.form.models.FormPreFillResponse
import ptml.releasing.form.models.base.BaseResponse
import java.io.File
import java.net.SocketTimeoutException

const val VALID = true
const val VALID_IMEI = "00000000"
const val INVALID_IMEI = ""
const val INVALID = false
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

fun mockConfiguration(): FormConfigureDeviceResponse {
    return FormConfigureDeviceResponse(mutableListOf())
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


fun provideImei(): String {
    return ""
}

val findCargoResponse: FormPreFillResponse by lazy {
    createFindCargoResponseSuccess()
}

val emptyValues = mapOf<Int?, Value>()

val emptyOptions = mapOf<Int?, Option>()


fun createFindCargoResponseSuccess(): FormPreFillResponse {
    val json = getJson("findCargoResponseSucess.json")
    return Gson().fromJson(json, FormPreFillResponse::class.java)
}

val emptyValuesList = listOf<Value>()
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
fun getTextBoxData(): FormConfiguration {
    return FormConfiguration(
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
fun buttonData(): FormConfiguration {
    return FormConfiguration(
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
fun getSingleDataWithOptions(): FormConfiguration {
    return FormConfiguration(
        SINGLE_SELECT_ID,
        Constants.SINGLE_SELECT,
        SINGLE_SELECT_TITLE,
        true,
        false,
        singleSelectOptions,
        ""
    )
}

fun getSingleDataNoOptions(): FormConfiguration {
    return FormConfiguration(
        SINGLE_SELECT_ID,
        Constants.SINGLE_SELECT,
        SINGLE_SELECT_TITLE,
        true,
        false,
        mutableListOf(),
        ""
    )
}

fun getSingleDataOneOptions(): FormConfiguration {
    return FormConfiguration(
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
fun provideQuickRemarkData(): FormConfiguration {
    return FormConfiguration(
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
fun provideMultiSelectData(): FormConfiguration {
    return FormConfiguration(
        MULTI_SELECT_ID,
        Constants.MULTI_SELECT,
        MULTI_SELECT_NAME,
        true,
        false,
        provideMultiSelectOptions(),
        ""
    )
}


fun provideMultiSelectDataWithOneOption(): FormConfiguration {
    return FormConfiguration(
        MULTI_SELECT_ID,
        Constants.MULTI_SELECT,
        MULTI_SELECT_NAME,
        true,
        false,
        listOf(optionsSample),
        ""
    )
}

fun provideMultiSelectDataNoOption(): FormConfiguration {
    return FormConfiguration(
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
fun provideCheckBoxData(): FormConfiguration {
    return FormConfiguration(
        CHECK_BOX_ID,
        Constants.CHECK_BOX,
        CHECK_BOX_NAME,
        true,
        false,
        listOf(),
        ""
    )
}


val configureDeviceData: List<FormConfiguration> by lazy {
    provideConfigureDeviceData()
}


val configureDeviceDataNonRequired: List<FormConfiguration> by lazy {
    provideConfigureDeviceDataNonRequired()
}

val configureDeviceDataSomeRequired: List<FormConfiguration> by lazy {
    provideConfigureDeviceDataSomeRequired()
}

val configureDeviceDataWithInvalidFormType: List<FormConfiguration> by lazy {
    configureDeviceDataWithInvalidFormType()
}


private fun configureDeviceDataWithInvalidFormType(): MutableList<FormConfiguration> {
    val list = configureDeviceData.toMutableList()
    val configureDeviceData = FormConfiguration(
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

private fun provideConfigureDeviceData(): List<FormConfiguration> {
    val json = getJson("setConfigDeviceSuccess.json")

    return Gson().fromJson(json, FormConfigureDeviceResponse::class.java).data
}

private fun provideConfigureDeviceDataNonRequired(): List<FormConfiguration> {
    val json = getJson("setConfigDeviceSuccessNonRequired.json")
    return Gson().fromJson(json, FormConfigureDeviceResponse::class.java).data
}

private fun provideConfigureDeviceDataSomeRequired(): List<FormConfiguration> {
    val json = getJson("setConfigDeviceSuccessSomeRequired.json")
    return Gson().fromJson(json, FormConfigureDeviceResponse::class.java).data
}

private fun getJson(path: String): String {
    val uri = BaseTest::class.java.classLoader?.getResource(path)
    val file = File(uri?.path ?: return "")
    return String(file.readBytes())
}






