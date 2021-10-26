package ptml.releasing.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.withContext
import ptml.releasing.BuildConfig
import ptml.releasing.app.data.domain.repository.ImeiRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by kryptkode on 10/23/2019.
 */
class ImeiHelper @Inject constructor(
    private val context: Context,
    private val imeiRepository: ImeiRepository,
    private val dispatchers: AppCoroutineDispatchers,
    @Named(Constants.DEBUG)
    private val debug: Boolean
) {


    @Suppress("DEPRECATION")
    @SuppressLint("HardwareIds")
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    suspend fun getImei(): String {
        val imei = if (BuildConfig.DEBUG) {
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

        return withContext(dispatchers.db) {
            Timber.d("Gotten IMEI: $imei")
            var savedImei = imeiRepository.getIMEI()
            Timber.d("Saved IMEI: $savedImei")
            if (savedImei.isEmpty()) {
                imeiRepository.setIMEI(imei)
                savedImei = imei
            }

            Timber.d("Saved IMEI: $savedImei")
            savedImei
        }

    }

}