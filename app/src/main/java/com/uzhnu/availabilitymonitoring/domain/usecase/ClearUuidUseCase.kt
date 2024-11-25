package com.uzhnu.availabilitymonitoring.domain.usecase

interface ClearUuidUseCase {

    suspend fun invoke(uuid: String? = null)

}
