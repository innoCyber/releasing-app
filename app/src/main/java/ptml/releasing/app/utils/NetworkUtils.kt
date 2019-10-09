package ptml.releasing.app.utils

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

class NetworkUtils @Inject constructor(private val context: Context) {
    @Suppress("DEPRECATION")
    fun isOffline(): Boolean {
        val manager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return !(manager.activeNetworkInfo != null && manager.activeNetworkInfo.isConnectedOrConnecting)
    }
}