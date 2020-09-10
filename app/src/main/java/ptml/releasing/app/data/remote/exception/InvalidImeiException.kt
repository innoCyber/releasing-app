package ptml.releasing.app.data.remote.exception

import java.io.IOException

class InvalidImeiException(override var message: String) : IOException(message)