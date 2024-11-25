package com.uzhnu.availabilitymonitoring.presentation.service.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.uzhnu.availabilitymonitoring.R
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.presentation.ui.activity.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PowerCheckerNotifications @Inject constructor(
    private val context: Context,
    private val applicationLogger: ApplicationLogger
) {
    init {
        createNotificationChannel()
    }

    private val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        action = Intent.ACTION_MAIN
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    fun sendServiceStartedNotification() {
        with(NotificationManagerCompat.from(context)) {
            cancel(NOTIFICATION_ID_SERVICE_STOPPED)
            sendNotification(
                this,
                NOTIFICATION_ID_SERVICE_STARTED,
                context.getString(R.string.notification_service_started_title),
                context.getString(R.string.notification_service_started_message),
                SEND_SERVICE_STARTED_MESSAGE
            )
        }
    }

    fun sendServiceStoppedNotification() {
        with(NotificationManagerCompat.from(context)) {
            cancel(NOTIFICATION_ID_SERVICE_STARTED)
            sendNotification(
                this,
                NOTIFICATION_ID_SERVICE_STOPPED,
                context.getString(R.string.notification_service_stopped_title),
                context.getString(R.string.notification_service_stopped_message),
                SEND_SERVICE_STOPPED_MESSAGE
            )
        }
    }

    fun sendServiceRunningNotification() {
        sendNotification(
            NotificationManagerCompat.from(context),
            NOTIFICATION_ID_SERVICE_RUNNING,
            context.getString(R.string.notification_service_running_title),
            context.getString(R.string.notification_service_running_message),
            SEND_SERVICE_RUNNING_MESSAGE
        )
    }

    fun sendServiceRestartNotification() {
        sendNotification(
            NotificationManagerCompat.from(context),
            NOTIFICATION_ID_SERVICE_RESTART,
            context.getString(R.string.notification_service_restart_title),
            context.getString(R.string.notification_service_restart_message),
            SEND_SERVICE_RESTART_MESSAGE
        )
    }

    private fun sendNotification(
        notificationManager: NotificationManagerCompat,
        notificationId: Int,
        title: String,
        content: String,
        logMessage: String
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Handle permission on Android 13
            return
        }
        notificationManager.notify(
            notificationId,
            notificationBuilder(title, content).build()
        )
        applicationLogger.log(TAG, logMessage)
    }

    fun clearNotificationsFromTray() {
        cancelNotification(NOTIFICATION_ID_SERVICE_STARTED)
        cancelNotification(NOTIFICATION_ID_SERVICE_RUNNING)
        cancelNotification(NOTIFICATION_ID_SERVICE_STOPPED)
        cancelNotification(NOTIFICATION_ID_SERVICE_RESTART)
    }

    private fun cancelNotification(id: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(id)
        }
    }

    @SuppressLint("InlinedApi")
    private fun notificationBuilder(title: String, content: String) =
        NotificationCompat.Builder(
            context,
            context.getString(R.string.power_checker_notification_channel_id)
        )
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setSmallIcon(R.drawable.ic_app_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.power_notification_chanel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(
                    context.getString(R.string.power_checker_notification_channel_id),
                    name,
                    importance
                )

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "PowerCheckerNotificationManager"

        private const val SEND_SERVICE_STARTED_MESSAGE = "send notification service started"
        private const val SEND_SERVICE_RUNNING_MESSAGE = "send notification service running"
        private const val SEND_SERVICE_STOPPED_MESSAGE = "send notification service stopped"
        private const val SEND_SERVICE_RESTART_MESSAGE = "send notification service restart"

        private const val NOTIFICATION_ID_SERVICE_STARTED = 1
        private const val NOTIFICATION_ID_SERVICE_RUNNING = 2
        private const val NOTIFICATION_ID_SERVICE_STOPPED = 3
        private const val NOTIFICATION_ID_SERVICE_RESTART = 4
    }
}
