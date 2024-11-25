package com.uzhnu.availabilitymonitoring.presentation.ui.screen.splash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.ExistingUuidState
import com.uzhnu.availabilitymonitoring.domain.usecase.GetUuidUseCase
import com.uzhnu.availabilitymonitoring.presentation.service.notification.PowerCheckerNotifications
import com.uzhnu.availabilitymonitoring.presentation.service.worker.DailyPeriodicalWorkerHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val uuidUseCase: GetUuidUseCase,
    private val applicationLogger: ApplicationLogger,
    powerCheckerNotifications: PowerCheckerNotifications,
    application: Application
) :
    ViewModel() {

    private val _uiState: MutableStateFlow<SplashUIState> = MutableStateFlow(
        SplashUIState.Loading
    )
    val uiState: StateFlow<SplashUIState> = _uiState.asStateFlow()

    private lateinit var uuidState: ExistingUuidState

    init {
        checkUser()
        powerCheckerNotifications.clearNotificationsFromTray()
        DailyPeriodicalWorkerHelper.scheduleWorker(application)
    }

    private fun checkUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val uuid = uuidUseCase.getExistingUuid()
            if (uuid is ExistingUuidState.CorrectUuid) {
                configureFirebaseNotifications()
                subscribeUserToReceiveTopicNotification(uuid.uuid)
            }
            uuidState = uuid
        }
    }

    private fun configureFirebaseNotifications() {
        Firebase.messaging.isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                applicationLogger.log(TAG, "$FIREBASE_FAILED_MESSAGE ${task.exception}")
                return@OnCompleteListener
            }

            val token = task.result

            applicationLogger.log(TAG, "$FIREBASE_SUCCESS_MESSAGE $token")
        })
    }

    private fun subscribeUserToReceiveTopicNotification(uuid: String) {
        Firebase.messaging.subscribeToTopic(uuid)
            .addOnCompleteListener { task ->
                var msg = FIREBASE_SUBSCRIBE_SUCCESS_MESSAGE
                if (!task.isSuccessful) {
                    msg = FIREBASE_SUBSCRIBE_FAIL_MESSAGE
                }
                applicationLogger.log(TAG, msg)
            }
    }

    fun onAnimationFinished() {
        if (::uuidState.isInitialized.not()) {
            _uiState.update { SplashUIState.Loading }
            return
        }
        when (uuidState) {
            is ExistingUuidState.CorrectUuid -> _uiState.update {
                SplashUIState.NavigateHome
            }
            is ExistingUuidState.EmptyUuid -> _uiState.update {
                SplashUIState.NavigateUuidInput
            }
            is ExistingUuidState.UnknownError -> _uiState.update {
                SplashUIState.Failure
            }
        }
    }

    companion object {
        private const val TAG = "Splash Screen"
        private const val FIREBASE_FAILED_MESSAGE = "Fetching FCM registration token failed"
        private const val FIREBASE_SUCCESS_MESSAGE =
            "Fetching FCM registration token succeed, token:"

        private const val FIREBASE_SUBSCRIBE_SUCCESS_MESSAGE = "Firebase topic successfully subscribed"
        private const val FIREBASE_SUBSCRIBE_FAIL_MESSAGE = "Firebase topic failed to subscribed"
    }
}

sealed class SplashUIState {
    object Loading : SplashUIState()
    object NavigateHome : SplashUIState()
    object NavigateUuidInput : SplashUIState()
    object Failure : SplashUIState()
}
