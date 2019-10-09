package ptml.releasing.app.utils.errorlogger

import okhttp3.Interceptor
import okhttp3.Response
import ptml.releasing.app.utils.NetworkUtils
import javax.inject.Inject

class InternetErrorLoggingInterceptor @Inject constructor(
    private val logger: Logger,
    private val networkUtils: NetworkUtils,
    private val descriptionUtils: DescriptionUtils) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url()
        if(networkUtils.isOffline()){
            logger.logError(descriptionUtils.getDescription(url.pathSegments().last()), url.toString(), "No internet connectivity")
        }
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logger.logError(descriptionUtils.getDescription(url.pathSegments().last()), url.toString(), e.localizedMessage)
            throw e
        }

        return response
    }
}