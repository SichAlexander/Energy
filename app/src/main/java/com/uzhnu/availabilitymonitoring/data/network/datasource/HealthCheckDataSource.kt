package com.uzhnu.availabilitymonitoring.data.network.datasource

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.getValue
import com.uzhnu.availabilitymonitoring.data.network.api.HealthCheckApi
import com.uzhnu.availabilitymonitoring.domain.model.RegionReport
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

    suspend fun generateReport(
        period: String,
        regionFilter: List<String> = emptyList() // Фільтр за регіонами
    ): Map<String, RegionReport> {
        val database = FirebaseDatabase.getInstance()
        val powerStatusRef = database.getReference("power_status")

        // Визначаємо часові межі
        val now = System.currentTimeMillis()
        val startTime = when (period) {
            RegionReport.TODAY -> getStartOfDay(now)
            RegionReport.WEEK -> now - 7 * 24 * 60 * 60 * 1000 // 7 днів тому
            RegionReport.WEEK -> now - 30 * 24 * 60 * 60 * 1000 // 30 днів тому
            else -> throw IllegalArgumentException("Unknown period: $period")
        }

        // Завантажуємо дані з Firebase
        val snapshot = powerStatusRef.get().await()
        val report = mutableMapOf<String, RegionReport>()

        // Обробка даних
        snapshot.children.forEach { regionSnapshot ->
            val regionId = regionSnapshot.key ?: return@forEach
            if (regionFilter.isNotEmpty() && regionId !in regionFilter) return@forEach // Фільтруємо регіони

            val regionData = regionSnapshot.children
                .filter { daySnapshot ->
                    val timestamp = parseDateToTimestamp(daySnapshot.key ?: "")
                    timestamp in startTime..now
                }
                .flatMap { it.children } // Отримуємо всі слоти дня
                .flatMap { it.children } // Отримуємо всі користувачі зі слотів

            val activeSlots = regionData.size
            val totalSlots =
                ((now - startTime) / (15 * 60 * 1000)).toInt() // Кількість 15-хвилинних слотів

            val percentageActive =
                if (totalSlots > 0) (activeSlots.toDouble() / totalSlots) * 100 else 0.0
            val inactiveMinutes = (totalSlots - activeSlots) * 15

            report[regionId] = RegionReport(
                regionId = regionId,
                activePercentage = percentageActive,
                avgInactiveHours = inactiveMinutes.toDouble() / regionData.size
            )
        }

        return report
    }

    // Парсинг дати (наприклад, "2024-11-25" в timestamp)
    fun parseDateToTimestamp(dateString: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            format.parse(dateString)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }

    }

    // Функція для отримання початку дня
    fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     *
     *     suspend fun analyzePowerStatus(): Map<String, RegionReport> {
     *         val database = FirebaseDatabase.getInstance()
     *         val powerStatusRef = database.getReference("power_status")
     *         val devicesRef = database.getReference("devices")
     *
     *         val devicesSnapshot = devicesRef.get().await()
     *         val powerStatusSnapshot = powerStatusRef.get().await()
     *
     *         val devicesByRegion = devicesSnapshot.children.groupBy(
     *             { it.child("region_id").value as String },
     *             { it.key ?: "" }
     *         )
     *
     *         val report = mutableMapOf<String, RegionReport>()
     *
     *         for (regionId in powerStatusSnapshot.children.map { it.key }) {
     *             if (regionId == null) continue
     *             val regionDevices = devicesByRegion[regionId] ?: emptyList()
     *             val totalDevices = regionDevices.size
     *
     *             if (totalDevices == 0) continue
     *
     *             val regionStatus = powerStatusSnapshot.child(regionId)
     *             val activeDevicesCount = regionStatus.children.flatMap { it.children }
     *                 .map { it.key }.toSet().size
     *             val totalSlots = regionStatus.childrenCount.toInt()
     *
     *             val activePercentage = if (totalDevices > 0) {
     *                 (activeDevicesCount.toDouble() / (totalDevices * totalSlots)) * 100
     *             } else 0.0
     *
     *             val avgInactiveHours = if (totalDevices > 0) {
     *                 ((totalDevices * totalSlots) - activeDevicesCount).toDouble() / totalDevices / 60.0
     *             } else 0.0
     *
     *             report[regionId] = RegionReport(
     *                 regionId = regionId,
     *                 activePercentage = activePercentage,
     *                 avgInactiveHours = avgInactiveHours
     *             )
     *         }
     *
     *         return report
     *     }
     */
}
