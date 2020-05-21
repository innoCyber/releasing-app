package ptml.releasing.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.multidex.MultiDex
import androidx.work.Configuration
import androidx.work.WorkManager
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import ptml.releasing.BuildConfig
import ptml.releasing.app.di.components.DaggerAppComponent
import ptml.releasing.app.utils.log.CrashReportingTree
import timber.log.Timber


open class ReleasingApplication : DaggerApplication() {
    open val appComponent by lazy {
        DaggerAppComponent
            .builder()
            .bindApplication(this)
            .build()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initializeLeakCanary()
        initWorkerFactory()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

    }


    private fun initializeLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }


    protected fun initWorkerFactory() {
        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(appComponent.workerFactory()).build()
        )
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission", "HardwareIds", "Deprecation")
    fun provideImei(): String {
          return when (BuildConfig.DEBUG) {
            true -> BuildConfig.IMEI
            else -> {
                val telephonyManager =
                    getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telephonyManager.imei ?: ""
                } else {
                    telephonyManager.deviceId
                }
            }
        }
    }

}