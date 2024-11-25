package com.uzhnu.availabilitymonitoring.presentation.service.batterystatus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BatteryStatusReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            append("isCharging: ${BatteryStatusCheckup.getStatusFromIntent(intent)}\n")

            Log.d(TAG, toString())
        }
    }

    companion object {
        private val TAG = BatteryStatusReceiver::class.qualifiedName
    }
}
