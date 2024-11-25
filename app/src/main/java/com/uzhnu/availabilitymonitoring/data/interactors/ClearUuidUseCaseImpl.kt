package com.uzhnu.availabilitymonitoring.data.interactors

import com.uzhnu.availabilitymonitoring.data.datastorage.UserPreferencesRepository
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.PowerChecker
import com.uzhnu.availabilitymonitoring.domain.usecase.ClearUuidUseCase
import com.uzhnu.availabilitymonitoring.domain.usecase.SendLogFilesUseCase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearUuidUseCaseImpl @Inject constructor(
    private val userPreferences: UserPreferencesRepository,
    private val applicationLogger: ApplicationLogger,
    private val powerChecker: PowerChecker,
    private val sendLogsUseCase: SendLogFilesUseCase
) : ClearUuidUseCase {

    override suspend fun invoke(uuid: String?): Unit = runCatching {
        userPreferences.clearUserUuid()
        powerChecker.cancelCheckup()
    }.fold(onSuccess = {
        applicationLogger.log(TAG, CLEAR_MESSAGE_SUCCESS)
        uuid?.let {  sendLogsUseCase.invoke(it) }
    }, onFailure = {
        FirebaseCrashlytics.getInstance().recordException(it)
        applicationLogger.log(TAG, CLEAR_MESSAGE_FAILURE)
    })

    companion object {
        private const val TAG = "ClearUuidUseCase"
        private const val CLEAR_MESSAGE_SUCCESS = "uuid was deleted"
        private const val CLEAR_MESSAGE_FAILURE = "delete uuid failed"
    }
}
