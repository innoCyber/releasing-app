package ptml.releasing.app.utils.errorlogger

import ptml.releasing.app.remote.Endpoint
import javax.inject.Inject

class DescriptionUtils @Inject constructor(){

    fun getDescription(encodedPath: String?): String {
        return Endpoint.fromPath(encodedPath).description
    }

}