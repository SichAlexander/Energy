package com.uzhnu.availabilitymonitoring.data.repository

import com.uzhnu.availabilitymonitoring.data.network.datasource.ServerDataSource
import com.uzhnu.availabilitymonitoring.domain.repository.ServerRepository
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRepositoryImpl @Inject constructor(private val api: ServerDataSource) :
    ServerRepository {

    override suspend fun sendLog(uuid: String, log: File): Result<Unit> {
        val response = api.sendLogs(uuid, log.readText(Charsets.ISO_8859_1))

        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(HttpException(response))
        }

    }
}
