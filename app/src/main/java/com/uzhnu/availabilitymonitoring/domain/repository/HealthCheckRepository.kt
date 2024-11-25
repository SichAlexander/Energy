package com.uzhnu.availabilitymonitoring.domain.repository

interface HealthCheckRepository {

    suspend fun pingBatteryCharging(uuid: String): Result<UuidNetworkStatus>

    suspend fun checkUserUuid(uuid: String) : Result<UuidNetworkStatus>
}

enum class UuidNetworkStatus{
    Valid,
    NotValid
}
