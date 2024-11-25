package com.uzhnu.availabilitymonitoring.presentation.service

import com.uzhnu.availabilitymonitoring.domain.model.PowerChecker
import com.uzhnu.availabilitymonitoring.presentation.service.alarm.PowerCheckerAlarm
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PowerCheckerModule {

    @Singleton
    @Binds
    abstract fun bindPowerChecker(powerChecker: PowerCheckerAlarm): PowerChecker
}
