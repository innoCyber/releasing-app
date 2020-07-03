package ptml.releasing.save_time_worker

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*


class SaveTimeWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    var prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    override fun doWork(): Result {

        return try {
            isInactiveInLastHour()
              Log.d("TimeWorker","Hello")
            Result.success()
        } catch (throwable: Throwable) {
            Log.d("TimeWorker","Result Failure")
            Result.failure()
        }
    }

    private fun isInactiveInLastHour(): Boolean {
        val currentDate = Calendar.getInstance().timeInMillis
        val savedTime = prefs.getLong(DATE_TIME ,0)
        val timediff =currentDate - savedTime
        Log.e("TimeDiff" , "${timediff}")

        return timediff> 3600000

    }
    companion object {
        const val DATE_TIME = "date_time"
    }
}