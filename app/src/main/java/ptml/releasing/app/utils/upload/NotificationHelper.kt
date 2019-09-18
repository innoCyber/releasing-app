package ptml.releasing.app.utils.upload

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color

import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

import ptml.releasing.R
import timber.log.Timber


/**
 * Helper class to manage notification channels, and create notifications.
 */
class NotificationHelper(mContext: Context) : ContextWrapper(mContext) {
    private var manager: NotificationManager? = null

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private val smallIcon: Int
        get() = android.R.drawable.stat_notify_sync


    init {
        val channel: NotificationChannel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                UPLOAD_CHANNEL,
                getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.lightColor = Color.GREEN
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            getManager().createNotificationChannel(channel)
        }
    }

    fun getProgressNotification(
        title: String,
        body: String,
        progress: Int
    ): NotificationCompat.Builder {
        Timber.w("Progress in notification: $progress")
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(
                applicationContext,
                UPLOAD_CHANNEL
            )

        builder.color = ContextCompat.getColor(applicationContext, R.color.colorAccent)
        builder.setSmallIcon(smallIcon)
            .setGroup(GROUP_KEY)
            .setOnlyAlertOnce(true) //This prevents the notification from playing sounds or vibrating for each progress update on >API 27
            .setContentTitle(title)
            .setContentText(body)
            .setOngoing(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setProgress(100, progress, false)
        return builder
    }




     fun getSummaryNotification(
        title: String,
        body: String
    ): NotificationCompat.Builder {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(
                applicationContext,
                UPLOAD_CHANNEL
            )

        val bigStyle = NotificationCompat.BigTextStyle()
        bigStyle.setBigContentTitle(title)
        bigStyle.bigText(body)
        builder.setSmallIcon(smallIcon)
        builder.setStyle(bigStyle)
        builder.setGroup(GROUP_KEY)
        builder.setOnlyAlertOnce(true)
        builder.setGroupSummary(true)
        return builder
    }




    fun getNotification(
        title: String,
        body: String,
        resultPendingIntent: PendingIntent
    ): NotificationCompat.Builder {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(
                applicationContext,
                UPLOAD_CHANNEL
            )

        val bigStyle = NotificationCompat.BigTextStyle()
        bigStyle.setBigContentTitle(title)
        bigStyle.bigText(body)
        builder.setSmallIcon(smallIcon)
        builder.setStyle(bigStyle)
        builder.setGroup(GROUP_KEY)
        builder.color = ContextCompat.getColor(applicationContext, R.color.colorAccent)
        builder.setContentIntent(resultPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL).priority = NotificationCompat.PRIORITY_HIGH
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        builder.setOnlyAlertOnce(true)
        return builder
    }

    /**
     * Send a notification.
     *
     * @param id           The ID of the notification
     * @param notification The notification object
     */
    fun notify(id: Int, notification: NotificationCompat.Builder) {
        getManager().notify(id, notification.build())
    }

    /**
     * Get the notification manager.
     * @return The system service NotificationManager
     */
    private fun getManager(): NotificationManager {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager!!
    }

    fun cancelNotification(notificationId: Int) {
        getManager().cancel(notificationId)
    }

    companion object {
        const val UPLOAD_CHANNEL = "default"
        const val SUMMARY_NOTIFICATION_ID = 100

        const val GROUP_KEY = "PROGRESS_GROUP_KEY"
    }
}
