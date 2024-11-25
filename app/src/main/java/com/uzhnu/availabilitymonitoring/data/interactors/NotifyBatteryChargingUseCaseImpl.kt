package com.uzhnu.availabilitymonitoring.data.interactors

import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.ExistingUuidState
import com.uzhnu.availabilitymonitoring.domain.model.UuidState
import com.uzhnu.availabilitymonitoring.domain.repository.HealthCheckRepository
import com.uzhnu.availabilitymonitoring.domain.repository.UuidNetworkStatus
import com.uzhnu.availabilitymonitoring.domain.usecase.ClearUuidUseCase
import com.uzhnu.availabilitymonitoring.domain.usecase.GetUuidUseCase
import com.uzhnu.availabilitymonitoring.domain.usecase.NotifyBatteryChargingUseCase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotifyBatteryChargingUseCaseImpl @Inject constructor(
    private val getUuidUseCase: GetUuidUseCase,
    private val clearUuidUseCase: ClearUuidUseCase,
    private val healthCheckRepository: HealthCheckRepository,
    private val applicationLogger: ApplicationLogger
) : NotifyBatteryChargingUseCase {

    override suspend fun invoke(): UuidState {
        return when (val uuid = getUuidUseCase.getExistingUuid()) {
            is ExistingUuidState.CorrectUuid -> {
                pingHealthCheck(uuid.uuid)
            }
            is ExistingUuidState.EmptyUuid -> UuidState.EmptyUuid
            is ExistingUuidState.UnknownError -> UuidState.UnknownError(uuid.throwable)
        }
    }

    private suspend fun pingHealthCheck(uuid: String): UuidState {
        return runCatching { healthCheckRepository.pingBatteryCharging(uuid) }
            .fold(onSuccess = { result ->
                result.fold(onSuccess = { uuidStatus ->
                    if (uuidStatus == UuidNetworkStatus.Valid) {
                        UuidState.CorrectUuid(uuid)
                    } else {
                        clearUuidUseCase.invoke()
                        UuidState.NotValidUuid
                    }
                }, onFailure = {
                    FirebaseCrashlytics.getInstance().recordException(it)
                    applicationLogger.log(TAG, it.stackTraceToString())
                    UuidState.UnknownError(it)
                })

            }, onFailure = {
                FirebaseCrashlytics.getInstance().recordException(it)
                applicationLogger.log(TAG, it.stackTraceToString())
                it.checkInternetConnection()
            })
    }

    companion object {
        private const val TAG = "NotifyBatteryCharging"
    }
}



