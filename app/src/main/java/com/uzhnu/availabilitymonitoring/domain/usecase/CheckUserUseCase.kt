package com.uzhnu.availabilitymonitoring.domain.usecase

import com.uzhnu.availabilitymonitoring.domain.model.UuidState

interface CheckUserUseCase {

    suspend fun validateUuid(uuid: String): UuidState

}

