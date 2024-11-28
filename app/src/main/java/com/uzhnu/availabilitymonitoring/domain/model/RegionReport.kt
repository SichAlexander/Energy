package com.uzhnu.availabilitymonitoring.domain.model

// Модель для збереження статистики регіону
data class RegionReport(
    val regionId: String,
    val activePercentage: Double,
    val avgInactiveHours: Double
){

    companion object {
        const val TODAY = "today"
        const val MONTH = "month"
        const val WEEK = "week"
    }
}
