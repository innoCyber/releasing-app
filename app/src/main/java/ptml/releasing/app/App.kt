package ptml.releasing.app

import com.squareup.leakcanary.LeakCanary

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import ptml.releasing.BuildConfig
import ptml.releasing.di.components.DaggerReleasingAppComponent
import timber.log.Timber

class App: DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerReleasingAppComponent.builder().bindApplication(this).build()
    }


    override fun onCreate() {
        super.onCreate()
        initLogger()
        initializeLeakCanary()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

        }


    }


    private fun initializeLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

}