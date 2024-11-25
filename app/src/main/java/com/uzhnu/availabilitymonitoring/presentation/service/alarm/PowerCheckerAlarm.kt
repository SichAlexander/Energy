package com.uzhnu.availabilitymonitoring.presentation.service.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_INCLUDE_STOPPED_PACKAGES
import com.uzhnu.availabilitymonitoring.domain.model.PowerChecker
import com.uzhnu.availabilitymonitoring.presentation.service.broadcastreceivers.PowerConnectionAlarmReceiver
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.presentation.service.notification.PowerCheckerNotifications
import com.uzhnu.availabilitymonitoring.presentation.service.utils.DateUtils.millisToDate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PowerCheckerAlarm @Inject constructor(
    @ApplicationContext private val context: Context,
    private val applicationLogger: ApplicationLogger,
    private val notifications: PowerCheckerNotifications
) : PowerChecker {

    override fun startCheckup() {
        if (isServiceRunning().not()) {
            notifications.sendServiceStartedNotification()
        }
        scheduleAlarm()
    }

    override fun scheduleNextCheckup() {
        scheduleAlarm()
    }

    override fun cancelCheckup() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val isServicePrevRunning = isServiceRunning()
        val alarmIntent = getAlarmPendingIntent()

        alarmManager.cancel(alarmIntent)
        alarmIntent.cancel()

        if (isServicePrevRunning) {
            notifications.sendServiceStoppedNotification()
        }
    }

    override fun isCheckupRunning(): Boolean {
        return isServiceRunning()
    }

    @SuppressLint("InlinedApi")
    private fun isServiceRunning(): Boolean {
        val alarmIntent = PendingIntent.getBroadcast(
            context, ALARM_REQUEST_CODE,
            getAlarmIntent(),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        return alarmIntent != null
    }

    private fun scheduleAlarm() {
        val alarmManager =
            context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = getAlarmPendingIntent()
        alarmManager.cancel(alarmIntent)

        val executionTime = System.currentTimeMillis() + ALARM_INTERVAL
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            executionTime,
            alarmIntent
        )

        applicationLogger.log(
            TAG,
            "Alarm scheduled for time: ${executionTime.millisToDate()}"
        )
    }

    @SuppressLint("InlinedApi")
    private fun getAlarmPendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(
            context.applicationContext,
            ALARM_REQUEST_CODE,
            getAlarmIntent(),
            PendingIntent.FLAG_IMMUTABLE
        )

    private fun getAlarmIntent(): Intent =
        Intent(context.applicationContext, PowerConnectionAlarmReceiver::class.java).also {
            it.action = POWER_ALARM_ACTION
            it.flags = FLAG_INCLUDE_STOPPED_PACKAGES
        }

    companion object {
        private const val ALARM_REQUEST_CODE = 528
        private const val POWER_ALARM_ACTION = "com.uzhnu.availabilitymonitoring.POWER_ALARM_ACTION"
        private const val ALARM_INTERVAL = 60 * 1000
        private val TAG = PowerCheckerAlarm::class.java.simpleName

        fun isPowerAlarmIntent(intent: Intent?): Boolean =
            if (intent == null) {
                false
            } else {
                intent.action == POWER_ALARM_ACTION
            }
    }
}

