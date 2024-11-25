package com.uzhnu.availabilitymonitoring.domain.model

interface PowerChecker {

    fun startCheckup()

    fun scheduleNextCheckup()

    fun cancelCheckup()

    fun isCheckupRunning(): Boolean
}
