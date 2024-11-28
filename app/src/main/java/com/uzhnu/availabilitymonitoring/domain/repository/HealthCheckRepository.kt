package com.uzhnu.availabilitymonitoring.domain.repository

import com.uzhnu.availabilitymonitoring.domain.model.RegionReport

interface HealthCheckRepository {

    suspend fun pingBatteryCharging(uuid: String): Result<UuidNetworkStatus>

    suspend fun checkUserUuid(uuid: String): Result<UuidNetworkStatus>

    suspend fun generateReport(
        period: String,
        regionFilter: List<String> = emptyList()
    ): Pair<Result<UuidNetworkStatus>, Map<String, RegionReport>>
}

enum class UuidNetworkStatus {
    Valid,
    NotValid
}
