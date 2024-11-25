package com.uzhnu.availabilitymonitoring.data.network.datasource

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.getValue
import com.uzhnu.availabilitymonitoring.data.network.api.HealthCheckApi
import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthCheckDataSource @Inject constructor(private val api: HealthCheckApi) {

    suspend fun checkUserUuid(userId: String): Boolean {
        val database = FirebaseDatabase.getInstance()
        val devicesRef = database.getReference("devices").child(userId)

        return try {
            // Отримуємо інформацію про пристрій
            val deviceSnapshot = devicesRef.get().await()
            if (deviceSnapshot.exists()) {
                // Перевіряємо, чи є дані для даного пристрою
                val regionId = deviceSnapshot.child("region_id").value as? String
                if (regionId != null) {
                    println("Device found. Region: $regionId")
                    return true // Пристрій знайдений
                } else {
                    println("Device region_id is missing.")
                    return false // Пристрій знайдений, але region_id відсутній
                }
            } else {
                println("Device not found for userId: $userId")
                return false // Пристрій не знайдено
            }
        } catch (e: Exception) {
            println("Error checking device: ${e.message}")
            return false // Сталася помилка при перевірці
        }
    }

    suspend fun pingBatteryCharging(userId: String): Boolean {
        val database = FirebaseDatabase.getInstance()
        val devicesRef = database.getReference("devices").child(userId)
        val powerStatusRef = database.getReference("power_status")

        return try {
            // Отримуємо інформацію про пристрій
            val deviceSnapshot = devicesRef.get().await()
            if (!deviceSnapshot.exists()) {
                println("Device not found for userId: $userId")
                return false
            }

            // Витягуємо region_id
            val regionId = deviceSnapshot.child("region_id").value as? String
                ?: throw IllegalStateException("Region ID is missing for device: $userId")

            // Генеруємо поточний timestamp
            val currentTimestamp = System.currentTimeMillis()
            val calendar = Calendar.getInstance().apply { timeInMillis = currentTimestamp }
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            val timeSlot = SimpleDateFormat("HH-mm", Locale.getDefault()).format(calendar.time)

            // Додаємо статус
            val regionRef = powerStatusRef.child(regionId).child(date).child(timeSlot)
            regionRef.push().setValue(userId).await()

            println("Power status added for user: $userId, region: $regionId, time: $timeSlot")
            true
        } catch (e: Exception) {
            println("Error adding power status: ${e.message}")
            false
        }
    }

    suspend fun generateReport(date: String): Map<String, Map<String, Int>> {
        val database = FirebaseDatabase.getInstance()
        val powerStatusRef = database.getReference("power_status/$date")

        val report = mutableMapOf<String, Map<String, Int>>()

        val regionsSnapshot = powerStatusRef.get().await()

        regionsSnapshot.children.forEach { regionSnapshot ->
            val regionId = regionSnapshot.key ?: return@forEach
            val regionData = mutableMapOf<String, Int>()

            regionSnapshot.children.forEach { timeSnapshot ->
                val timeSlot = timeSnapshot.key ?: return@forEach
                val users = timeSnapshot.getValue(object : GenericTypeIndicator<List<String>>() {}) ?: listOf()

                regionData[timeSlot] = users.size
            }

            report[regionId] = regionData
        }

        return report
    }
}
