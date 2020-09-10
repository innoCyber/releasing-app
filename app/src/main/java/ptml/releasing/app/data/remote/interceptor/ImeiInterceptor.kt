package ptml.releasing.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import ptml.releasing.app.data.remote.exception.InvalidImeiException
import javax.inject.Inject

class ImeiInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url()
        if(url.queryParameterNames().contains("imei")){
            val imei = request.url().queryParameter("imei")
            checkIfValidImeiOrThrow(imei)
        }
        return chain.proceed(request)
    }

    private fun checkIfValidImeiOrThrow(imei: String?) {
        if(validImei(imei).not()){
            throw InvalidImeiException("Missing or invalid IMEI")
        }
    }

    private fun validImei(imei: String?): Boolean {
        return imei.isNullOrEmpty().not() //TODO: Better handle validation
    }
}