package ptml.releasing.app.data.remote

/**
 * Created by kryptkode on 12/27/2019.
 */
enum class Endpoint(
    val url: String,
    val description: String
) {
    LOGIN(Endpoints.LOGIN, "User Login"),
    RESET_PASSWORD(
        Endpoints.RESET_PASSWORD,
        "Reset user password"
    ),
    UNKNOWN("", "An unknown path");

    companion object {
        fun fromPath(path: String?): Endpoint {
            return when (path) {
                Endpoints.LOGIN -> LOGIN
                Endpoints.RESET_PASSWORD -> RESET_PASSWORD
                else -> UNKNOWN
            }
        }
    }

}