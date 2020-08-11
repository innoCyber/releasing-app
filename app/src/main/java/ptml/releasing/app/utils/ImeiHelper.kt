package ptml.releasing.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ptml.releasing.BuildConfig
import ptml.releasing.app.data.domain.usecase.SetImeiUseCase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

/**
 * Created by kryptkode on 10/23/2019.
 */
class ImeiHelper @Inject constructor(
    private val context: Context,
    private val setImeiUseCase: SetImeiUseCase,
    private val dispatchers: AppCoroutineDispatchers,
    @Named(Constants.DEBUG)
    private val debug: Boolean
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = dispatchers.db

    @Suppress("DEPRECATION")
    @SuppressLint("HardwareIds")
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getImei(): String {
        val imei = if (debug) {
            BuildConfig.IMEI
        } else {
            val telephonyManager = getSystemService(context, TelephonyManager::class.java)
            when {
                //return empty IMEI for running on android 10 and greater, the IMEI would be set by the admin
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    ""
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    telephonyManager?.imei ?: ""
                }
                else -> {
                    telephonyManager?.deviceId ?: ""
                }
            }
        }

        Timber.d("Gotten IMEI: $imei")
        //save
        launch {
            setImeiUseCase.execute(SetImeiUseCase.Params(imei))
        }
        return imei
    }

}