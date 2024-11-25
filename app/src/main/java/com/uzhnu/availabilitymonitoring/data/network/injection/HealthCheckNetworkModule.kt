package com.uzhnu.availabilitymonitoring.data.network.injection

import com.uzhnu.availabilitymonitoring.BuildConfig
import com.uzhnu.availabilitymonitoring.data.network.api.HealthCheckApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HealthCheckNetworkModule {

    @Singleton
    @Provides
    fun provideHealthCheckApi(
        retrofit: Retrofit.Builder
    ): HealthCheckApi {
        return retrofit
            .baseUrl(provideHealthCheckApiUrl())
            .build().create(HealthCheckApi::class.java)
    }

    @Provides
    fun provideHealthCheckApiUrl(): String = BuildConfig.LINK_HEALTH_CHECK
}
