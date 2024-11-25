package com.uzhnu.availabilitymonitoring.presentation.service.notification

import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.ExistingUuidState
import com.uzhnu.availabilitymonitoring.domain.model.PowerChecker
import com.uzhnu.availabilitymonitoring.domain.usecase.GetUuidUseCase
import com.uzhnu.availabilitymonitoring.domain.usecase.SendLogFilesUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var applicationLogger: ApplicationLogger

    @Inject
    lateinit var powerChecker: PowerChecker

    @Inject
    lateinit var powerCheckerNotifications: PowerCheckerNotifications

    @Inject
    lateinit var sendLogFilesUseCase: SendLogFilesUseCase

    @Inject
    lateinit var uuidUseCase: GetUuidUseCase

    private val sendLogsJob = SupervisorJob()


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        applicationLogger.log(TAG, RECEIVE_MESSAGE)
        powerCheckerNotifications.sendServiceRestartNotification()
        powerChecker.startCheckup()
    }

    override fun onNewToken(token: String) {
        applicationLogger.log(TAG, "$NEW_TOKEN_MESSAGE $token")
        sendLogs()
    }

    private fun sendLogs() {
        CoroutineScope(sendLogsJob).launch(Dispatchers.IO) {
            when (val uuid = uuidUseCase.getExistingUuid()) {
                is ExistingUuidState.CorrectUuid -> sendLogFilesUseCase.invoke(uuid = uuid.uuid)
                else -> {
                    applicationLogger.log(TAG, MESSAGE_UUID_MISSING)
                }
            }
        }
    }

    override fun onDestroy() {
        sendLogsJob.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "Firebase Messaging"

        private const val RECEIVE_MESSAGE = "notification received"
        private const val NEW_TOKEN_MESSAGE = "new token received. token:"
        private const val MESSAGE_UUID_MISSING = "uuid missing"
    }
}
