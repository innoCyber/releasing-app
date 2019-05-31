package ptml.releasing.app.utils.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import timber.log.Timber


class NetworkListener(private val context: Context) : LifecycleObserver {
    private val _networkSubject = MutableLiveData<Boolean>()
    private val _networkInfoLive = MutableLiveData<NetworkStateWrapper>()

    val networkStateLive: LiveData<Boolean> = _networkSubject
    val networkInfoLive: LiveData<NetworkStateWrapper> = _networkInfoLive

    private lateinit var receiver: BroadcastReceiver
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val connectivityManager =
        context.applicationContext.getSystemService<ConnectivityManager>()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupConnectionListener()
        } else {
            initializeReceiver()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val connectivityManager =
                context.applicationContext.getSystemService<ConnectivityManager>()
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } else {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupConnectionListener() {
        Timber.d("Setting up connectivity listener")
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network?) {
                Timber.d(" lost connection")
                val networkInfo = connectivityManager?.getNetworkInfo(network)
                val networkStateWrapper = NetworkStateWrapper(false, networkInfo)
                _networkInfoLive.postValue(networkStateWrapper)
                _networkSubject.postValue(false)
            }

            override fun onAvailable(network: Network?) {
                Timber.d(" gained connection")
                val networkInfo = connectivityManager?.getNetworkInfo(network)
                val networkStateWrapper = NetworkStateWrapper(true, networkInfo)
                _networkInfoLive.postValue(networkStateWrapper)
                _networkSubject.postValue(true)
            }
        }
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
    }


    @Suppress("DEPRECATION")
    private fun initializeReceiver() {
        receiver = ConnectivityReceiver()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter)
    }


    @Suppress("DEPRECATION")
    inner class ConnectivityReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null) {
                val stateChange = isOffline()
                Timber.d("Network state changed to: %s", stateChange)
                val networkStateWrapper =
                    NetworkStateWrapper(stateChange, connectivityManager?.activeNetworkInfo)
                _networkInfoLive.postValue(networkStateWrapper)
                _networkSubject.postValue(stateChange)
            }
        }
    }

    @Suppress("DEPRECATION")
    fun isOffline(): Boolean {
        return !(connectivityManager?.activeNetworkInfo != null
                && connectivityManager.activeNetworkInfo.isConnectedOrConnecting)
    }

}

data class NetworkStateWrapper(val connected: Boolean, val networkInfo: NetworkInfo?)
