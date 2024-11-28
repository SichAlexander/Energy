package com.uzhnu.availabilitymonitoring.domain.model

sealed class GenerateReportState {
    object EmptyUuid : ReportState()
    object NotValidUuid : ReportState()
    data class CorrectUuid(val uuid: String) : ReportState()
    object NoConnection : ReportState()
    object NoInternet : ReportState()
    data class UnknownError(val throwable: Throwable) : ReportState()
}

sealed class ReportState {
    object EmptyResult : ReportState()
    data class SuccessResult(val pdfPath: String) : ReportState()
    data class UnknownError(val throwable: Throwable) : ReportState()
    data class NoError(val throwable: Throwable) : ReportState()
}

