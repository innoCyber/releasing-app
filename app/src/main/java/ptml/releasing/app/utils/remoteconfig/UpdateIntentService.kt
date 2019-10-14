package ptml.releasing.app.utils.remoteconfig

import android.content.Context
import android.content.Intent
import dagger.android.DaggerIntentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class UpdateIntentService : DaggerIntentService(UpdateIntentService::class.java.name), CoroutineScope {
    @Inject
    lateinit var repository: Repository
    @Inject
    lateinit var appCoroutineDispatchers: AppCoroutineDispatchers
    @Inject
    lateinit var remoteConfigManger: RemoteConfigManger


    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = appCoroutineDispatchers.network + job


    companion object {
        private const val ACTION_UPDATE_DAMAGES = "action_update_damages"
        private const val ACTION_UPDATE_QUICK_REMARKS = "action_update_quick_remarks"
        fun startUpdateDamages(context: Context) {
            Timber.d("Start update service for damages")
            val intent = Intent(context, UpdateIntentService::class.java)
            intent.action = ACTION_UPDATE_DAMAGES
            context.startService(intent)
        }

        fun startUpdateQuickRemarks(context: Context) {
            Timber.d("start update service for quick remarks")
            val intent = Intent(context, UpdateIntentService::class.java)
            intent.action = ACTION_UPDATE_QUICK_REMARKS
            context.startService(intent)
        }
    }


    override fun onHandleIntent(intent: Intent?) {
        //update
        Timber.w("Start of onHandleIntent")
        when (intent?.action) {
            ACTION_UPDATE_QUICK_REMARKS -> {
                Timber.d("Start update for quick remarks")
                updateQuickRemarks()
            }

            ACTION_UPDATE_DAMAGES -> {
                Timber.d("Start update for damages")
                updateDamages()
            }
        }

        Timber.w("End of onHandleIntent")
    }



    private fun updateQuickRemarks() {
        launch {
            try {
                val imei = repository.getImei()
                repository.downloadQuickRemarkAsync(imei ?: return@launch)?.await()

                val quickRemarkVersion = remoteConfigManger.quickRemarkVersion
                Timber.d("Downloaded quick remark, updating the local quick remark version to $quickRemarkVersion")
                repository.setQuickCurrentVersion(quickRemarkVersion)
            } catch (t: Throwable) {
                Timber.e(t, "Error occurred while trying to update quick remark")
            }
        }
    }

    private fun updateDamages() {
        launch {
            try {
                val imei = repository.getImei()
                repository.downloadDamagesAsync(imei ?: return@launch)?.await()

                val damagesVersion = remoteConfigManger.damagesVersion
                Timber.d("Downloaded damages, updating the local damages version to $damagesVersion")
                repository.setDamagesCurrentVersion(damagesVersion)
            } catch (t: Throwable) {
                Timber.e(t, "Error occurred while trying to update damages")
            }
        }
    }

}