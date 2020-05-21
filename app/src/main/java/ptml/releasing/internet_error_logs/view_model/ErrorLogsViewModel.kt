package ptml.releasing.internet_error_logs.view_model

import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.getExternalStorageDirectory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.db.model.InternetErrorLogModel
import ptml.releasing.app.utils.*
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.internet_error_logs.model.ErrorCache
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.util.*
import javax.inject.Inject


class ErrorLogsViewModel @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    private val errorCache: ErrorCache,
    updateChecker: RemoteConfigUpdateChecker,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    val errorLogs = errorCache.getErrors()

    private val exportLoadingState = MutableLiveData<NetworkState>()
    fun getExportLoadingState(): LiveData<NetworkState> = exportLoadingState

    private val checkStoragePermission = MutableLiveData<Event<Unit>>()
    fun getCheckStoragePermission(): LiveData<Event<Unit>> = checkStoragePermission

    private val shareExportedFile = MutableLiveData<Event<File>>()
    fun getShareExportedFile(): LiveData<Event<File>> = shareExportedFile

    fun handleExportClick() {
        Timber.d("Export clicked, checking permissions")
        checkStoragePermission.postValue(Event(Unit))
    }

    fun exportLogsToCSV() {
        Timber.d("Permissions granted, exporting logs...")
        exportLoadingState.postValue(NetworkState.LOADING)

        compositeJob = CoroutineScope(dispatchers.db).launch {
            val logs = errorCache.getAllLogs()

            val exportDir = File(getExternalStorageDirectory(), DIRECTORY_DOWNLOADS)
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val file = File(exportDir, generateRandomFileName())
            try {
                file.createNewFile()
                val csvWrite = CSVWriter(FileWriter(file))
                //write the headings...
                Timber.d("Export logs, writing headings.")
                csvWrite.writeNext(
                    arrayOf(
                        InternetErrorLogModel.COLUMN_ID,
                        InternetErrorLogModel.COLUMN_DATE,
                        InternetErrorLogModel.COLUMN_ERROR,
                        InternetErrorLogModel.COLUMN_DESCRIPTION,
                        InternetErrorLogModel.COLUMN_URL
                    )
                )

                //write the contents
                Timber.d("Export logs, writing contents.")
                for (i in logs.indices) {
                    val log = logs[i]
                    csvWrite.writeNext(
                        arrayOf(
                            (i + 1).toString(),
                            dateTimeUtils.formatDate(log.date),
                            log.error,
                            log.description,
                            log.url
                        )
                    )
                }
                csvWrite.close()
                shareExportedFile.postValue(Event(file))
                exportLoadingState.postValue(NetworkState.LOADED)
            } catch (sqlEx: Exception) {
                exportLoadingState.postValue(NetworkState.error(sqlEx.localizedMessage))
            }

        }
    }


    private fun generateRandomFileName(): String {
        return "${dateTimeUtils.formatExportDate(Date())}.csv"
    }
}