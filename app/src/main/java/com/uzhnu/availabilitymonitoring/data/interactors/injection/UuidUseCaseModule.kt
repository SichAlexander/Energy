package com.uzhnu.availabilitymonitoring.data.interactors.injection

import com.uzhnu.availabilitymonitoring.data.interactors.CheckUserUseCaseImpl
import com.uzhnu.availabilitymonitoring.data.interactors.ClearUuidUseCaseImpl
import com.uzhnu.availabilitymonitoring.data.interactors.GetUuidUseCaseImpl
import com.uzhnu.availabilitymonitoring.domain.usecase.CheckUserUseCase
import com.uzhnu.availabilitymonitoring.domain.usecase.ClearUuidUseCase
import com.uzhnu.availabilitymonitoring.domain.usecase.GetUuidUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class UuidUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindCheckUserUseCase(useCaseImpl: CheckUserUseCaseImpl): CheckUserUseCase

    @Singleton
    @Binds
    abstract fun bindGetUuidUseCase(useCaseImpl: GetUuidUseCaseImpl): GetUuidUseCase

    @Singleton
    @Binds
    abstract fun bindClearUuidUseCase(useCaseImpl: ClearUuidUseCaseImpl): ClearUuidUseCase

}
