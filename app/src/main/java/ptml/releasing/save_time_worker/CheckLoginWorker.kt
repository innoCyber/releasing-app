package ptml.releasing.save_time_worker

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.withContext
import ptml.releasing.R
import ptml.releasing.app.data.domain.usecase.LogOutUseCase
import ptml.releasing.app.data.local.LocalDataManager
import ptml.releasing.app.di.modules.worker.ChildWorkerFactory
import ptml.releasing.app.eventbus.EventBus
import ptml.releasing.app.utils.AppCoroutineDispatchers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit


class CheckLoginWorker @AssistedInject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val eventBus: EventBus,
    private val logOutUseCase: LogOutUseCase,
    private val localDataManager: LocalDataManager,
    @Assisted
    private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        return try {

            withContext(dispatchers.main) {
                //eventBus.send(LoginSessionTimeoutEvent())
                event.postValue(System.currentTimeMillis())
                Toast.makeText(
                    applicationContext,
                    applicationContext.getString(R.string.session_timed_out_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
            Result.success()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error occurred while executing ${this::class.java.simpleName}")
            Result.retry()
        }
    }


    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    companion object {
        const val CHECK_LOGIN_TAG = "CHECK_LOGIN_TAG"
        private const val CHECK_LOGIN_NAME = "CHECK_LOGIN_NAME"
        private const val TEN_MIN_MILLIS = 10 * 60 * 1000L
        val event: MutableLiveData<Long> = MutableLiveData(0L)

        fun isMoreThan5Min(time: Long): Boolean {
            val now = Calendar.getInstance().timeInMillis
            val timeDiff = now - time
            return timeDiff > TEN_MIN_MILLIS
        }

        fun scheduleWork(context: Context): UUID {
            val workRequest = OneTimeWorkRequestBuilder<CheckLoginWorker>()
                .setInitialDelay(TEN_MIN_MILLIS, TimeUnit.MILLISECONDS)
                .addTag(CHECK_LOGIN_TAG)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(CHECK_LOGIN_NAME,
                ExistingWorkPolicy.REPLACE, workRequest)

            Timber.d("scheduling CheckLoginWorker work done.")
            return workRequest.id
        }

    }
}