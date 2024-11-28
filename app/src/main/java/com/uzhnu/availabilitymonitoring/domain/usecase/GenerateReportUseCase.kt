package com.uzhnu.availabilitymonitoring.domain.usecase

import com.uzhnu.availabilitymonitoring.domain.model.ReportState

interface GenerateReportUseCase {

    suspend fun generateReport(period: String, filterRegion : List<String>): ReportState

}

