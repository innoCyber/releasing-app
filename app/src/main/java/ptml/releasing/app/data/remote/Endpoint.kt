package ptml.releasing.app.data.remote

import ptml.releasing.driver.app.data.remote.Endpoints

/**
 * Created by kryptkode on 12/27/2019.
 */
enum class Endpoint(
    val url: String,
    val description: String
) {
    LOGIN(Endpoints.LOGIN, "User Login"),

    GET_OPERATION_AND_TERMINAL_LISTS(
        Endpoints.GET_OPERATION_AND_TERMINAL_LISTS,
        "Get operation step and terminals list"
    ),

    SET_CONFIGURATION_DEVICE(Endpoints.SET_CONFIGURATION_DEVICE, "Download damages"),
    UNKNOWN("", "An unknown path");

    companion object {
        fun fromPath(path: String?): Endpoint {
            return when (path) {
                Endpoints.LOGIN -> LOGIN

                Endpoints.GET_OPERATION_AND_TERMINAL_LISTS -> GET_OPERATION_AND_TERMINAL_LISTS
                Endpoints.SET_CONFIGURATION_DEVICE -> SET_CONFIGURATION_DEVICE
                else -> UNKNOWN
            }
        }
    }

}