package com.uzhnu.availabilitymonitoring.data.network.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HealthCheckApi {

    @GET("/{uuid}")
    suspend fun pingBatteryCharging(
        @Path("uuid") uuid: String
    ): Response<ResponseBody>

    @GET("/{uuid}/log")
    suspend fun checkUserUuid(
        @Path("uuid") uuid: String
    ): Response<ResponseBody>
}
