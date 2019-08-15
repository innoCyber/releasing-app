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

    fun getNotification(title: String, body: String, progress: Int): NotificationCompat.Builder {
        Timber.w("Progress in nogification: $progress")
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(
                applicationContext,
                UPLOAD_CHANNEL
            )

        builder.setSmallIcon(smallIcon)
        builder.color = ContextCompat.getColor(applicationContext, R.color.colorAccent)
        builder.setContentTitle(title)
            .setOngoing(true)
            //.setContentIntent(resultPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL).priority = NotificationCompat.PRIORITY_HIGH
        builder.setVibrate(longArrayOf(0L))
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        builder.setProgress(100, progress, false)
        /*if (progress == 100) {
            builder.setProgress(0, 0, false)
//            builder.setContentText(body)
        }*/
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
        builder.color = ContextCompat.getColor(applicationContext, R.color.colorAccent)
        builder.setContentIntent(resultPendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL).priority = NotificationCompat.PRIORITY_HIGH
        builder.setVibrate(longArrayOf(0L))
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

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
     *
     *
     * Utility method as this helper works with it a lot.
     *
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
        val UPLOAD_CHANNEL = "default"
        val NOTIFICATION_ID = 100
    }
}
