package com.uzhnu.availabilitymonitoring.data.repository

import com.uzhnu.availabilitymonitoring.domain.repository.HealthCheckRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HealthCheckRepositoryModule {

    @Binds
    abstract fun bindHealthCheckRepository(repository: HealthCheckRepositoryImpl): HealthCheckRepository
}
