package com.uzhnu.availabilitymonitoring.domain.usecase

import com.uzhnu.availabilitymonitoring.domain.model.UuidState

interface NotifyBatteryChargingUseCase {

    suspend operator fun invoke(): UuidState

}
