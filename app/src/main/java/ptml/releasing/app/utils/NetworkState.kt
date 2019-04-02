
package ptml.releasing.app.utils

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
        val status: Status,
        val msg: String? = null,
        val throwable: Throwable? = null) {
    companion object {
        @JvmStatic
        val LOADED = NetworkState(Status.SUCCESS)
        @JvmStatic
        val LOADING = NetworkState(Status.RUNNING)
        @JvmStatic
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)

        @JvmStatic
        fun error(msg: Throwable?) = NetworkState(Status.FAILED, throwable = msg)
    }
}