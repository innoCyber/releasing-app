package ptml.releasing.save_time_worker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class SaveTimeViewModel(application: Application): AndroidViewModel(application) {

   var workManager = WorkManager.getInstance(application)



    internal fun saveWork() {
        var saveTime = PeriodicWorkRequest.Builder(
            SaveTimeWorker::class.java, 15, TimeUnit.MINUTES
        )

        val saveWork = saveTime.build()
        workManager.enqueue(saveWork)


    }

}

