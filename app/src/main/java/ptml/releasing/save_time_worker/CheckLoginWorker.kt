package ptml.releasing.save_time_worker

import android.content.Context
import android.widget.Toast
import androidx.work.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.withContext
import ptml.releasing.R
import ptml.releasing.app.data.domain.usecase.LogOutUseCase
import ptml.releasing.app.data.local.LocalDataManager
import ptml.releasing.app.di.modules.worker.ChildWorkerFactory
import ptml.releasing.app.eventbus.EventBus
import ptml.releasing.app.eventbus.LoginSessionTimeoutEvent
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
    @Assisted private val params: WorkerParameters) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        return try {
            val lastActiveTime = localDataManager.getLastActiveTime()
            if(isMoreThanAnHour(lastActiveTime)){
                Timber.d("Time is more than an hour.. Logging out")
                eventBus.send(LoginSessionTimeoutEvent())
                //log out
                logOutUseCase.execute()
                withContext(dispatchers.main){
                    Toast.makeText(applicationContext, applicationContext.getString(R.string.session_timed_out_msg), Toast.LENGTH_SHORT).show()
                }
                Result.success()
            }else {
                Timber.d("Time is not more than more than an hour.. Moving on")
                Result.retry()
            }
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error occurred while executing ${this::class.java.simpleName}")
            Result.retry()
        }
    }

    private fun isMoreThanAnHour(time:Long):Boolean{
        val now = Calendar.getInstance().timeInMillis
        val timeDiff = now - time
        return timeDiff > ONE_HOUR_MILLIS
    }

    private fun rescheduleWork() {
        scheduleWork(applicationContext)
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    companion object {
        private const val ONE_HOUR_MILLIS = 3600000L
        private const val DEFAULT_INTERVAL_SECS = 3600L
        private const val CHECK_LOGIN_INTERVAL_TYPE = Calendar.SECOND
        private const val CHECK_LOGIN_TAG = "CHECK_LOGIN_TAG"
        private const val CHECK_LOGIN_NAME = "CHECK_LOGIN_NAME"

        fun scheduleWork(
            context: Context,
            intervalInSecs: Long = DEFAULT_INTERVAL_SECS
        ): UUID {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            dueDate.add(CHECK_LOGIN_INTERVAL_TYPE, intervalInSecs.toInt())

            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
            Timber.d("Work scheduled..., to be executed in ${timeDiff / 1000} seconds")
            return scheduleWorkInternal(context, timeDiff)
        }

        private fun scheduleWorkInternal(context: Context, delayInMillis: Long): UUID {
            Timber.d("scheduling CheckLoginWorker worker...")

            val workRequest = OneTimeWorkRequestBuilder<CheckLoginWorker>()
                .addTag(CHECK_LOGIN_TAG)
                .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(CHECK_LOGIN_NAME, ExistingWorkPolicy.KEEP, workRequest)

            Timber.d("scheduling CheckLoginWorker work done.")
            return workRequest.id
        }

        fun cancelWork(context: Context): Operation {
            return WorkManager.getInstance(context).cancelAllWorkByTag(CHECK_LOGIN_TAG)
        }
    }
}