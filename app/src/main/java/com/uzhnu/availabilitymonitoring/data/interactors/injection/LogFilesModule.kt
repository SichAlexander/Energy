package com.uzhnu.availabilitymonitoring.data.interactors.injection

import com.uzhnu.availabilitymonitoring.data.interactors.SendLogFilesUseCaseImpl
import com.uzhnu.availabilitymonitoring.domain.usecase.SendLogFilesUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class LogFilesModule {

    @Singleton
    @Binds
    abstract fun bingSendLogFilesUseCase(useCaseImpl: SendLogFilesUseCaseImpl)
            : SendLogFilesUseCase

}
