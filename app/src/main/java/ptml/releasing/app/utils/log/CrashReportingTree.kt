package ptml.releasing.app.utils.log

import android.util.Log

import com.crashlytics.android.Crashlytics

import timber.log.Timber

import android.util.Log.INFO

/** A tree which logs important information for crash reporting.  */
class CrashReportingTree : Timber.Tree() {
    public override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priority >= INFO
    }


    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        Crashlytics.log(priority, tag, message)

        if (t != null) {
            if (priority == Log.ERROR) {
                Crashlytics.logException(t)
            }
        }
    }


}
