package com.uzhnu.availabilitymonitoring.domain.repository

import java.io.File

interface ServerRepository {

    suspend fun sendLog(uuid: String, log: File): Result<Unit>

}

