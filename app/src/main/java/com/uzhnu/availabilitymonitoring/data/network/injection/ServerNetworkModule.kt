package com.uzhnu.availabilitymonitoring.data.network.injection

import com.uzhnu.availabilitymonitoring.BuildConfig
import com.uzhnu.availabilitymonitoring.data.network.api.LogsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServerNetworkModule {

    @Singleton
    @Provides
    fun provideServerApi(
        retrofit: Retrofit.Builder
    ): LogsApi {
        return retrofit
            .baseUrl(provideServerUrl())
            .build().create(LogsApi::class.java)
    }

    private fun provideServerUrl(): String = BuildConfig.LINK_SERVER_API

}
