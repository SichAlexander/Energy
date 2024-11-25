package com.uzhnu.availabilitymonitoring.data.interactors.injection

import com.uzhnu.availabilitymonitoring.data.interactors.NotifyBatteryChargingUseCaseImpl
import com.uzhnu.availabilitymonitoring.domain.usecase.NotifyBatteryChargingUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class BatteryUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindNotifyBatteryChargingUseCase(useCaseImpl: NotifyBatteryChargingUseCaseImpl)
            : NotifyBatteryChargingUseCase

}
