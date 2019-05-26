package ptml.releasing.quick_remarks.model.api

import kotlinx.coroutines.Deferred
import ptml.releasing.quick_remarks.model.QuickRemarkResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface QuickRemarkService {

    @GET("downloadQuickRemarks")
    fun downloadQuickRemarksAsync(@Query("imei") imei: String): Deferred<QuickRemarkResponse>
}