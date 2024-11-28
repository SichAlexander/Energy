package com.uzhnu.availabilitymonitoring.data.interactors

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.ReportState
import com.uzhnu.availabilitymonitoring.domain.repository.HealthCheckRepository
import com.uzhnu.availabilitymonitoring.domain.repository.UuidNetworkStatus
import com.uzhnu.availabilitymonitoring.domain.usecase.GenerateReportUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerateReportUseCaseImpl @Inject constructor(
    private val healthCheckRepository: HealthCheckRepository,
    private val applicationLogger: ApplicationLogger
) : GenerateReportUseCase {


    suspend fun generateAndSaveReport(period: String, regionFilter: List<String> = emptyList()): ReportState {
        val reportResult = healthCheckRepository.generateReport(period, regionFilter)
        return  reportResult.first.fold(onSuccess = { uuidStatus ->
            if (uuidStatus == UuidNetworkStatus.Valid) {
                // Генеруємо звіт
//                val report = generateReport(period, regionFilter)
                // Генеруємо PDF

                val pdfFile = applicationLogger.generatePdf(reportResult.second, period)

                println("PDF збережено: ${pdfFile.absolutePath}")
                ReportState.SuccessResult(pdfFile.absolutePath)
            } else {
                ReportState.EmptyResult
            }
        }, onFailure = {
            applicationLogger.log(TAG, it.stackTraceToString())
            FirebaseCrashlytics.getInstance().recordException(it)
            ReportState.UnknownError(it)
        })
    }

    companion object {
        private const val TAG = "GenerateReport"
    }

    override suspend fun generateReport(
        period: String,
        filterRegion: List<String>
    ): ReportState =
        runCatching { generateAndSaveReport(period, filterRegion) }.fold(
            onSuccess = { it },
            onFailure = {
                applicationLogger.log(TAG, it.stackTraceToString())
                FirebaseCrashlytics.getInstance().recordException(it)
                ReportState.EmptyResult
            })

}




