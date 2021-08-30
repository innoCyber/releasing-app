package ptml.releasing.quick_remarks.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.app.remote.Urls
import ptml.releasing.quick_remarks.model.QuickRemarkResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface QuickRemarkService {

    @GET(Urls.QUICK_REMARK) //the method was renamed to this, no idea why
     fun downloadQuickRemarksAsync(
        @Header("Authorization") authorization: String,
        @Query ( "imei") imei: String): Deferred<QuickRemarkResponse>
}


