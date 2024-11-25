package com.uzhnu.availabilitymonitoring.presentation.service.permissions

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService

const val TAG_PERMISSIONS = "Permissions Helper"

fun getPermissionsState(context: Context): String {
    val battery = isIgnoringBatteryOptimizations(context)
    val alarm = isAlarmPermissionGranted(context)
    val notification = isNotificationPermissionGranted(context)
    return "is Ignoring Battery Optimisations = $battery, is allowed to schedule alarm = $alarm, " +
            "is notification permission granted = $notification"
}

private fun isNotificationPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

private fun isAlarmPermissionGranted(context: Context): Boolean {
    val alarmManager = getSystemService(context, AlarmManager::class.java)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager?.canScheduleExactAlarms() == true
    } else {
        true
    }
}

private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        pm?.isIgnoringBatteryOptimizations(context.packageName) == true
    } else {
        true
    }
}


