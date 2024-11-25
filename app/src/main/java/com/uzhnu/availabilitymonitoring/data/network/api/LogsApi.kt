package com.uzhnu.availabilitymonitoring.data.network.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LogsApi {

    @POST("/api/service/log")
    suspend fun sendLogs(
        @Header("authorization_key") authorizationKey: String,
        @Header("Content-Type") contentType: String,
        @Query("uuid") uuid: String, @Body logs: RequestBody
    ): Response<ResponseBody>
}
