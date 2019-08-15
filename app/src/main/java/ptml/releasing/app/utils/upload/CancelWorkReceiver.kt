package ptml.releasing.app.utils.upload

import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import dagger.android.DaggerBroadcastReceiver
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.Constants
import java.util.*
import javax.inject.Inject

class CancelWorkReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var repository: Repository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        /**
         * Handle notification user actions
         */
       val  notificationHelper = NotificationHelper(context)
        val notificationId = intent.getIntExtra(Constants.UPLOAD_NOTIFICATION_ID, 0)
        val cargoCode = intent.getStringExtra(Constants.UPLOAD_NOTIFICATION_CARGO_CODE)
        when (intent.action) {
            ACTION_CANCEL -> {
                notificationHelper.cancelNotification(notificationId)
                cargoCode.let {
                    val workId = repository.getWorkerId(it)
                    WorkManager.getInstance(context).cancelWorkById(UUID.fromString(workId))
                }
            }
        }
    }

    companion object {
        val ACTION_RETRY = "ptml.releasing.app.utils.ACTION_RETRY"
        val ACTION_CANCEL = "ptml.releasing.app.utils.ACTION_CANCEL"
    }
}
