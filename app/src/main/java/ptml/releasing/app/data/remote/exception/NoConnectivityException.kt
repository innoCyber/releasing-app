package ptml.releasing.app.data.remote.exception

import java.io.IOException

/**
 * Created by kryptkode on 10/23/2019.
 */
class NoConnectivityException(override var message: String) : IOException(message)
