package com.uzhnu.availabilitymonitoring.presentation.service.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.uzhnu.availabilitymonitoring.domain.model.PowerChecker
import com.uzhnu.availabilitymonitoring.domain.usecase.NotifyBatteryChargingUseCase
import com.uzhnu.availabilitymonitoring.presentation.service.alarm.PowerCheckerAlarm
import com.uzhnu.availabilitymonitoring.presentation.service.batterystatus.BatteryStatusCheckup
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.UuidState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PowerConnectionAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var checker: PowerChecker

    @Inject
    lateinit var logger: ApplicationLogger

    @Inject
    lateinit var batteryCharging: NotifyBatteryChargingUseCase

    override fun onReceive(context: Context, intent: Intent) {
        if (PowerCheckerAlarm.isPowerAlarmIntent(intent).not()) {
            return
        }

        checkBatteryStatus(context)
        checker.scheduleNextCheckup()
    }

    private fun checkBatteryStatus(context: Context) {
        BatteryStatusCheckup.checkStatus(context.applicationContext).also { status ->
            val log = "isCharging: ${status.isCharging}"
            logger.log(TAG, log)

            if (status.isCharging || true) {
                pingServer()
            }
        }
    }

    private fun pingServer() = goAsync {
        when (val pingResult = batteryCharging.invoke()) {
            is UuidState.EmptyUuid, UuidState.NotValidUuid -> stopChecking()
            is UuidState.UnknownError -> logger.log(TAG, pingResult.throwable.message ?: "")
            is UuidState.NoConnection, UuidState.NoInternet -> logger.log(TAG, NO_CONNECTION)
            is UuidState.CorrectUuid -> logger.log(TAG, SUCCESS)
        }
    }

    private fun stopChecking() {
        logger.log(TAG, NOT_VALID_UUID)
        checker.cancelCheckup()
    }

    companion object {
        private const val TAG = "PowerCheckup"
        private const val NOT_VALID_UUID = "not valid uuid"
        private const val NO_CONNECTION = "no internet connection"
        private const val SUCCESS = "ping successful"
    }
}
