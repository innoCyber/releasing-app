package ptml.releasing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class ReleasingTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, "ptml.releasing.app.App", context)
    }
}