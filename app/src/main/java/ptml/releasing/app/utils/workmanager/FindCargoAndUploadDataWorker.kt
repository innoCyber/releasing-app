package ptml.releasing.app.utils.workmanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.Worker
import androidx.work.WorkerParameters
import ptml.releasing.cargo_search.data.data_source.ChassisDatabase
import ptml.releasing.cargo_search.data.data_source.ChassisNumberDao
import ptml.releasing.cargo_search.data.repository.ChassisNumberRepositoryImpl
import ptml.releasing.cargo_search.domain.model.ChassisNumber
import ptml.releasing.cargo_search.domain.repository.ChassisNumberRepository
import ptml.releasing.cargo_search.viewmodel.SearchViewModel

class FindCargoAndUploadDataWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    val chassisNumberRepository: ChassisNumberRepository

    init {
        val chassisNumberDao = ChassisDatabase.getDatabase(context).chassisNumberDao()
        chassisNumberRepository = ChassisNumberRepositoryImpl(chassisNumberDao)
    }
    override fun doWork(): Result {
        findCargoAndUpload()
        return Result.success()
    }

    private fun findCargoAndUpload() {
        // do upload work here
        val  chassisNumbers = chassisNumberRepository.getChassisNumbers().value
        for (i in chassisNumbers!!){

        }

        val uploadDataConstraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    }
}