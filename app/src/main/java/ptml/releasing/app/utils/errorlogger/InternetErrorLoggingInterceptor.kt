package ptml.releasing.app.utils.errorlogger

import okhttp3.Interceptor
import okhttp3.Response
import ptml.releasing.app.local.Local
import ptml.releasing.app.remote.Urls
import ptml.releasing.app.utils.NetworkUtils
import javax.inject.Inject

class InternetErrorLoggingInterceptor @Inject constructor(
    private val logger: Logger,
    private val networkUtils: NetworkUtils,
    private val local: Local,
    private val descriptionUtils: DescriptionUtils) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url()
        if(networkUtils.isOffline() && local.isInternetErrorLoggingEnabled()){
            logger.logError(descriptionUtils.getDescription(url.pathSegments().last()),
                if(url.toString().contains(Urls.LOGIN)) Urls.getUrlWithoutParameters(url.toString()) else url.toString(),  //log full url for other endpoints except the login endpoint to hide passwords
                "No internet connectivity")
        }
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            if(local.isInternetErrorLoggingEnabled()){
                logger.logError(descriptionUtils.getDescription(url.pathSegments().last()),
                    if(url.toString().contains(Urls.LOGIN)) Urls.getUrlWithoutParameters(url.toString()) else url.toString(),  //log full url for other endpoints except the login endpoint to hide passwords
                    e.localizedMessage)
            }
            throw e
        }

        return response
    }
}