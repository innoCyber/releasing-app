package ptml.releasing.app.utils.log

import android.util.Log.INFO
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/** A tree which logs important information for crash reporting.  */
class CrashReportingTree : Timber.Tree() {
    private val crashlytics = FirebaseCrashlytics.getInstance()
    public override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priority >= INFO
    }


    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        crashlytics.log("$priority/$tag: $message")
        t?.let {
            crashlytics.recordException(it)
        }
    }


}
