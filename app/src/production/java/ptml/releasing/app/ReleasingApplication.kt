package ptml.releasing.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.multidex.MultiDex
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import ptml.releasing.BuildConfig
import ptml.releasing.app.di.components.DaggerAppComponent
import timber.log.Timber


open class ReleasingApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .bindApplication(this).build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        initLogger()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission", "HardwareIds", "Deprecation")
    fun provideImei(): String {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager.imei
        } else {
            telephonyManager.deviceId
        }
    }

}