package ptml.releasing.app.data.remote

/**
 * Created by kryptkode on 12/27/2019.
 */
enum class Endpoint(
    val url: String,
    val description: String
) {
    LOGIN(Endpoints.LOGIN, "User Login"),
    UNKNOWN("", "An unknown path");

    companion object {
        fun fromPath(path: String?): Endpoint {
            return when (path) {
                Endpoints.LOGIN -> LOGIN
                else -> UNKNOWN
            }
        }
    }

}