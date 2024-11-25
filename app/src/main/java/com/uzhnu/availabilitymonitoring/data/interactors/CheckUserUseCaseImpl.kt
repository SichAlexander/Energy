package com.uzhnu.availabilitymonitoring.data.interactors

import com.uzhnu.availabilitymonitoring.data.datastorage.UserPreferencesRepository
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.UuidState
import com.uzhnu.availabilitymonitoring.domain.repository.HealthCheckRepository
import com.uzhnu.availabilitymonitoring.domain.repository.UuidNetworkStatus
import com.uzhnu.availabilitymonitoring.domain.usecase.CheckUserUseCase
import com.uzhnu.availabilitymonitoring.domain.usecase.ClearUuidUseCase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckUserUseCaseImpl @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
    private val clearUuidUseCase: ClearUuidUseCase,
    private val healthCheckRepository: HealthCheckRepository,
    private val applicationLogger: ApplicationLogger
) : CheckUserUseCase {

    override suspend fun validateUuid(uuid: String): UuidState =
        runCatching { checkUuidHealthCheck(uuid) }.fold(
            onSuccess = { it },
            onFailure = {
                applicationLogger.log(TAG, it.stackTraceToString())
                FirebaseCrashlytics.getInstance().recordException(it)
                it.checkInternetConnection()
            })


    private suspend fun checkUuidHealthCheck(uuid: String): UuidState =
        healthCheckRepository.checkUserUuid(uuid).fold(onSuccess = { uuidStatus ->
            if (uuidStatus == UuidNetworkStatus.Valid) {
                userPreferences.updateUserUuid(uuid)
                UuidState.CorrectUuid(uuid)
            } else {
                clearUuidUseCase.invoke()
                UuidState.NotValidUuid
            }
        }, onFailure = {
            applicationLogger.log(TAG, it.stackTraceToString())
            FirebaseCrashlytics.getInstance().recordException(it)
            UuidState.UnknownError(it)
        })

    companion object {
        private const val TAG = "CheckUserUser"
    }
}



