package com.uzhnu.availabilitymonitoring.presentation.service.broadcastreceivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.PowerChecker
import com.uzhnu.availabilitymonitoring.presentation.service.worker.DailyPeriodicalWorkerHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var logger: ApplicationLogger

    @Inject
    lateinit var powerChecker: PowerChecker

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (isBootCompletedIntent(intent = intent).not()) {
            return
        }
        logger.log(TAG, MESSAGE)

        DailyPeriodicalWorkerHelper.scheduleWorker(context.applicationContext)
        powerChecker.startCheckup()
    }

    @SuppressLint("InlinedApi")
    private fun isBootCompletedIntent(intent: Intent): Boolean {
        val action = intent.action
        return action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_LOCKED_BOOT_COMPLETED
    }

    companion object {
        private const val TAG = "BootCompleted"
        private const val MESSAGE = "device boot completed"
    }
}
