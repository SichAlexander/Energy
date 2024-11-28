package com.uzhnu.availabilitymonitoring.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.uzhnu.availabilitymonitoring.data.network.datasource.HealthCheckDataSource
import com.uzhnu.availabilitymonitoring.data.network.model.StatusCode
import com.uzhnu.availabilitymonitoring.domain.model.RegionReport
import com.uzhnu.availabilitymonitoring.domain.repository.HealthCheckRepository
import com.uzhnu.availabilitymonitoring.domain.repository.UuidNetworkStatus
import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthCheckRepositoryImpl @Inject constructor(private val api: HealthCheckDataSource) :
    HealthCheckRepository {

    override suspend fun pingBatteryCharging(uuid: String): Result<UuidNetworkStatus> {
        val response = api.pingBatteryCharging(uuid)
        sendChargingStatusForDevices()
        return processResponseCode(response)
    }

    override suspend fun checkUserUuid(uuid: String): Result<UuidNetworkStatus> {
        val response = api.checkUserUuid(uuid)
        return processResponseCode(response)
    }

    override suspend fun generateReport(period: String,
                                        regionFilter: List<String>): Pair<Result<UuidNetworkStatus>, Map<String, RegionReport>> {
        val response = api.generateReport(period, regionFilter)
//        val response = api.analyzePowerStatus()
        sendChargingStatusForDevices()
        return processResponseCode(response)
    }
    private fun processResponseCode(response: Boolean): Result<UuidNetworkStatus> {
        return if (response) Result.success(UuidNetworkStatus.Valid) else Result.success(UuidNetworkStatus.NotValid)
    }

    private fun processResponseCode(response: Map<String, RegionReport>): Pair<Result<UuidNetworkStatus>, Map<String, RegionReport>> {
        return if (response.isNotEmpty()) {
            Pair(Result.success(UuidNetworkStatus.Valid), response)
        } else {
            Pair(Result.success(UuidNetworkStatus.NotValid), response)
        }
    }

    suspend fun sendChargingStatusForDevices(devicesRef: String = "devices", powerStatusRef: String = "power_status") {
        val database = FirebaseDatabase.getInstance()
        val devicesSnapshot = database.getReference(devicesRef).get().await()
        val powerStatusDatabase = database.getReference(powerStatusRef)

        val devices = devicesSnapshot.children.map { it.key to it.child("region_id").value as String }
        val totalDevices = devices.size
        val ignoredCount = 5// 25% пристроїв для ігнорування

        // Випадковий вибір 25% пристроїв
        val ignoredDevices = devices.shuffled().take(ignoredCount).map { it.first }

        val currentTimestamp = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = currentTimestamp }
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val timeSlot = SimpleDateFormat("HH-mm", Locale.getDefault()).format(calendar.time)
        // Витягуємо region_id

        // Відправка статусу для решти 75% пристроїв
        devices.forEach { (deviceId, regionId) ->
            if (deviceId !in ignoredDevices) {
                deviceId?.let { powerStatusDatabase.child(regionId).child(date).child(timeSlot)
                    .push().setValue(deviceId).await()}

            }
        }

        println("Статус заряджання відправлено для ${totalDevices - ignoredCount} пристроїв. ${ignoredCount} пристроїв проігноровано.")
    }





    // Форматування дати для Firebase
    fun formatDate(timestamp: Long): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formatter.format(timestamp)
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
