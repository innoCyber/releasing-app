package ptml.releasing.app.di.modules.worker

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.workers.ConstraintTrackingWorker
import javax.inject.Inject

class ConstraintWorkerFactory @Inject constructor() : ChildWorkerFactory {
    @SuppressLint("RestrictedApi")
    override fun create(context: Context, params: WorkerParameters): ListenableWorker {
        return ConstraintTrackingWorker(context, params)
    }
}