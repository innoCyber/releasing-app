package ptml.releasing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import ptml.releasing.app.TestApplication

class ReleasingTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}