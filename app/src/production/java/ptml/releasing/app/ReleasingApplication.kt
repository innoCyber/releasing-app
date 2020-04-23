package ptml.releasing.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import ptml.releasing.BuildConfig
import ptml.releasing.app.di.components.DaggerAppComponent
import ptml.releasing.app.di.modules.network.NetworkModule
import timber.log.Timber


open class ReleasingApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .bindNetwork(NetworkModule())
            .bindApplication(this).build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initFabric()
    }

    private fun initFabric() {
        Fabric.with(this, Crashlytics(), CrashlyticsCore())
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