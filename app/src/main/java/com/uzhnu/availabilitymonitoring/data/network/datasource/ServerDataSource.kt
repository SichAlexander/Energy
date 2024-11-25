package com.uzhnu.availabilitymonitoring.data.network.datasource

import com.uzhnu.availabilitymonitoring.BuildConfig
import com.uzhnu.availabilitymonitoring.data.network.api.LogsApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerDataSource @Inject constructor(private val api: LogsApi) {

    suspend fun sendLogs(uuid: String, log: String): Response<ResponseBody> {
        return api.sendLogs(
            authorizationKey = BuildConfig.SERVER_API_KEY,
            contentType = CONTENT_TYPE,
            uuid = uuid,
            logs = log.toRequestBody(CONTENT_TYPE.toMediaTypeOrNull())
        )
    }


    private companion object {
        const val CONTENT_TYPE = "text/plain"
    }
}
