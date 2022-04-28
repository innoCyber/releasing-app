package ptml.releasing.app.utils.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class FindCargoAndUploadDataWorker(
    context: Context, workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return Result.success()
    }

    private fun findCargoAndUpload(chassisNumber: String, imei: String) {

    }

}