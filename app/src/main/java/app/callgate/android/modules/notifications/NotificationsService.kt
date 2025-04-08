package app.callgate.android.modules.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import app.callgate.android.R

class NotificationsService(
    context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    private val icons = mapOf(
        NOTIFICATION_ID_LOCAL_SERVICE to R.drawable.ic_notification,
        NOTIFICATION_ID_WEBHOOK_WORKER to R.drawable.notif_webhook,
    )

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val descriptionText = context.getString(R.string.call_gate_notifications)
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    fun makeNotification(context: Context, id: Int, contentText: String): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getText(R.string.app_name))
            .setContentText(contentText)
            .setSmallIcon(icons[id] ?: R.drawable.ic_notification)
            .build()
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "call-gate"

        const val NOTIFICATION_ID_LOCAL_SERVICE = 1
        const val NOTIFICATION_ID_WEBHOOK_WORKER = 2
    }
}