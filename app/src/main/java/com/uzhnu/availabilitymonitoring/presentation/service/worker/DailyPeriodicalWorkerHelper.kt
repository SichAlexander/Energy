package com.uzhnu.availabilitymonitoring.presentation.service.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit

object DailyPeriodicalWorkerHelper {
    private const val DAILY_WORK_NAME = "availability_daily_work"
    private const val DAY_IN_HOURS = 24
    private const val DESIRED_WORKER_HOUR = 9

    fun scheduleWorker(appContext: Context) {
        val workManager = WorkManager.getInstance(appContext)

        val workRequest = PeriodicWorkRequestBuilder<DailyPeriodicalWorkManager>(
            DAY_IN_HOURS.toLong(),
            TimeUnit.HOURS
        ).setConstraints(createConstraints())
            .setInitialDelay(calculateDelay(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            DAILY_WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
    }

    private fun calculateDelay(): Long {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        dueDate.set(Calendar.HOUR_OF_DAY, DESIRED_WORKER_HOUR)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, DAY_IN_HOURS)
        }
        return dueDate.timeInMillis - currentDate.timeInMillis
    }

    private fun createConstraints(): Constraints {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)

        return constraints.build()
    }
}
