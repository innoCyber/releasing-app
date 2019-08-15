package ptml.releasing.app.utils.upload

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


import androidx.core.app.NotificationCompat

import java.util.Objects

import ptml.releasing.R
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.upload.NotificationHelper.Companion.NOTIFICATION_ID
import ptml.releasing.device_configuration.view.DeviceConfigActivity
import timber.log.Timber

class FileProgressReceiver : BroadcastReceiver() {

    lateinit var mNotificationHelper: NotificationHelper
    lateinit var notification: NotificationCompat.Builder

    override fun onReceive(context: Context, intent: Intent) {
        mNotificationHelper = NotificationHelper(context)

        // Get notification id
        val notificationId = intent.getIntExtra(Constants.UPLOAD_NOTIFICATION_ID, 1)
        val fileName = intent.getStringExtra(Constants.UPLOAD_NOTIFICATION_FILE_NAME)
        val status = intent.getStringExtra(Constants.UPLOAD_NOTIFICATION_STATUS)
        val cargoCode = intent.getStringExtra(Constants.UPLOAD_NOTIFICATION_CARGO_CODE)
        // Receive progress
        val progress = intent.getIntExtra(Constants.UPLOAD_NOTIFICATION_PROGRESS, 0)
        Timber.d("Progress: $progress")
        when (Objects.requireNonNull(intent.action)) {
            ACTION_CLEAR_NOTIFICATION -> {
                Timber.d("ACTION_CLEAR_NOTIFICATION")
                mNotificationHelper.cancelNotification(notificationId)
            }
            ACTION_UPLOADED -> {
                Timber.d("ACTION_UPLOADED")
                val resultIntent = Intent(context, DeviceConfigActivity::class.java)
                val resultPendingIntent = PendingIntent.getActivity(
                    context,
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                notification = mNotificationHelper.getNotification(
                    context.getString(R.string.message_upload_success),
                    context.getString(R.string.file_upload_successful, cargoCode),
                    resultPendingIntent
                )
                mNotificationHelper.notify(NOTIFICATION_ID, notification)
            }
            ACTION_PROGRESS_NOTIFICATION -> {
                Timber.d("ACTION_PROGRESS_NOTIFICATION")
                notification = mNotificationHelper.getNotification(
                    context.getString(R.string.uploading, status),
                    context.getString(R.string.in_progress, fileName),
                    progress
                )
                mNotificationHelper.notify(NOTIFICATION_ID, notification)
            }
            else -> {
            }
        }

    }

    companion object {
        val ACTION_CLEAR_NOTIFICATION = "ptml.releasing.ACTION_CLEAR_NOTIFICATION"
        val ACTION_PROGRESS_NOTIFICATION = "ptml.releasing.ACTION_PROGRESS_NOTIFICATION"
        val ACTION_UPLOADED = "ptml.releasing.ACTION_UPLOADED"
    }
}
