package com.uzhnu.availabilitymonitoring.presentation.service.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.ExistingUuidState
import com.uzhnu.availabilitymonitoring.domain.model.PowerChecker
import com.uzhnu.availabilitymonitoring.domain.usecase.GetUuidUseCase
import com.uzhnu.availabilitymonitoring.domain.usecase.SendLogFilesUseCase
import com.uzhnu.availabilitymonitoring.presentation.service.notification.PowerCheckerNotifications
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyPeriodicalWorkManager @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notification: PowerCheckerNotifications,
    private val powerChecker: PowerChecker,
    private val applicationLogger: ApplicationLogger,
    private val uuidCheck: GetUuidUseCase,
    private val sendLogFilesUseCase: SendLogFilesUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val uuid = uuidCheck.getExistingUuid()

        applicationLogger.log(TAG, LOG_MESSAGE)
        cleanupLogs()

        if (uuid is ExistingUuidState.CorrectUuid) {
            sendNotification()
            sendLogs(uuid.uuid)
        }
        return Result.success()
    }

    private fun cleanupLogs() {
        applicationLogger.cleanUpOldFiles()
    }

    private suspend fun sendLogs(uuid: String) {
        sendLogFilesUseCase.invoke(uuid)
    }

    private fun sendNotification() {
        if (powerChecker.isCheckupRunning()) {
            notification.sendServiceRunningNotification()
        } else {
            startPowerCheckup()
        }
    }

    private fun startPowerCheckup() {
        powerChecker.startCheckup()
    }

    companion object {
        private val TAG = DailyPeriodicalWorkManager::class.java.simpleName
        private const val LOG_MESSAGE = "daily work executed"
    }
}
