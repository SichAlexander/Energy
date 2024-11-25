package com.uzhnu.availabilitymonitoring.presentation.service.batterystatus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.uzhnu.availabilitymonitoring.domain.model.BatteryStatus

object BatteryStatusCheckup {
    private val receiver: BroadcastReceiver = BatteryStatusReceiver()

    fun checkStatus(context: Context): BatteryStatus {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context.registerReceiver(receiver, it)
        }.also {
            context.unregisterReceiver(receiver)
        }

        return getStatusFromIntent(batteryStatus)
    }

    fun getStatusFromIntent(batteryStatus: Intent?): BatteryStatus {
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL
        return BatteryStatus(isCharging)
    }
}

