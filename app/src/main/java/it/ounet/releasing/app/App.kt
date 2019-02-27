package it.ounet.releasing.app

import android.app.Application
import com.squareup.leakcanary.LeakCanary

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import it.ounet.releasing.BuildConfig
import it.ounet.releasing.di.components.DaggerReleasingAppComponent
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