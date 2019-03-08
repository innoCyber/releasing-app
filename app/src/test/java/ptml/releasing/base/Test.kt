package ptml.releasing.base

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


class Message {
    var text: String? = null
}

interface MessagesApi {

    @GET("/message")
    fun findMessage(@Query("q") value: String): Observable<Message>
}