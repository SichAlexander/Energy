package com.uzhnu.availabilitymonitoring.domain.usecase

interface SendLogFilesUseCase {
    suspend fun invoke(uuid: String): Result<Unit>
}
