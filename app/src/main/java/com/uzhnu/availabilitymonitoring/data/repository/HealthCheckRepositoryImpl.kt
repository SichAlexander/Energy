package com.uzhnu.availabilitymonitoring.data.repository

import com.uzhnu.availabilitymonitoring.data.network.datasource.HealthCheckDataSource
import com.uzhnu.availabilitymonitoring.data.network.model.StatusCode
import com.uzhnu.availabilitymonitoring.domain.repository.HealthCheckRepository
import com.uzhnu.availabilitymonitoring.domain.repository.UuidNetworkStatus
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthCheckRepositoryImpl @Inject constructor(private val api: HealthCheckDataSource) :
    HealthCheckRepository {

    override suspend fun pingBatteryCharging(uuid: String): Result<UuidNetworkStatus> {
        val response = api.pingBatteryCharging(uuid)
        return processResponseCode(response)
    }

    override suspend fun checkUserUuid(uuid: String): Result<UuidNetworkStatus> {
        val response = api.checkUserUuid(uuid)
        return processResponseCode(response)
    }
    private fun processResponseCode(response: Boolean): Result<UuidNetworkStatus> {
        return if (response) Result.success(UuidNetworkStatus.Valid) else Result.success(UuidNetworkStatus.NotValid)
    }

    private fun parseResponseMessage(response: Response<ResponseBody>): UuidNetworkStatus {
        val bodyString = response.body()?.string()
        val isValidId: Boolean =
            bodyString?.contains(NOT_FOUND_ERROR_MESSAGE, ignoreCase = true)?.not() ?: false
        return if (isValidId) {
            UuidNetworkStatus.Valid
        } else {
            UuidNetworkStatus.NotValid
        }
    }

    private companion object {
        const val NOT_FOUND_ERROR_MESSAGE = "not found"
    }
}

//private fun processResponseCode(response: Response<ResponseBody>): Result<UuidNetworkStatus> {
//    return Result.success(UuidNetworkStatus.Valid)
//    return when (response.code()) {
//        StatusCode.OK.code -> Result.success(parseResponseMessage(response))
//        StatusCode.NotFound.code, StatusCode.BadRequest.code -> Result.success(UuidNetworkStatus.NotValid)
//        else -> Result.failure(HttpException(response))
//    }
//}
