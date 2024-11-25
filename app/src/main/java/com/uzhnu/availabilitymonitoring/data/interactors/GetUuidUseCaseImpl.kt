package com.uzhnu.availabilitymonitoring.data.interactors

import com.uzhnu.availabilitymonitoring.data.datastorage.UserPreferencesRepository
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.ExistingUuidState
import com.uzhnu.availabilitymonitoring.domain.usecase.GetUuidUseCase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUuidUseCaseImpl @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
    private val applicationLogger: ApplicationLogger
) : GetUuidUseCase {

    override suspend fun getExistingUuid(): ExistingUuidState {
        return runCatching {
            userPreferences.uuid.first()
        }.fold(onSuccess = { uuid ->
            if (uuid.isNullOrEmpty()) {
                ExistingUuidState.EmptyUuid
            } else {
                ExistingUuidState.CorrectUuid(uuid)
            }
        }, onFailure = {
            applicationLogger.log(TAG, it.stackTraceToString())
            FirebaseCrashlytics.getInstance().recordException(it)
            ExistingUuidState.UnknownError(it)
        })
    }

    companion object {
        private const val TAG = "GetUuidUseCase"
    }
}



