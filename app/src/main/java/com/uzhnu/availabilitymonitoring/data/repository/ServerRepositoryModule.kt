package com.uzhnu.availabilitymonitoring.data.repository

import com.uzhnu.availabilitymonitoring.domain.repository.ServerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServerRepositoryModule {

    @Binds
    abstract fun bindServerRepository(repository: ServerRepositoryImpl): ServerRepository
}
