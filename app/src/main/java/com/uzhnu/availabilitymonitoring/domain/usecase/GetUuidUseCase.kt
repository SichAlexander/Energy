package com.uzhnu.availabilitymonitoring.domain.usecase

import com.uzhnu.availabilitymonitoring.domain.model.ExistingUuidState

interface GetUuidUseCase {

    suspend fun getExistingUuid(): ExistingUuidState

}
