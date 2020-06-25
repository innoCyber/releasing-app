package ptml.releasing.save_time_worker

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters


class SaveTimeWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    var prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    override fun doWork(): Result {

        return try {
              Log.d("TimeWorker","Hello")
            Result.success()
        } catch (throwable: Throwable) {
            Log.d("TimeWorker","Result Failure")
            Result.failure()
        }
    }


}