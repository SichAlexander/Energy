package com.uzhnu.availabilitymonitoring.presentation.ui.screen.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uzhnu.availabilitymonitoring.data.interactors.GenerateReportUseCaseImpl
import com.uzhnu.availabilitymonitoring.data.interactors.SendLogFilesUseCaseImpl
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import com.uzhnu.availabilitymonitoring.domain.model.ExistingUuidState
import com.uzhnu.availabilitymonitoring.domain.model.GenerateReportState
import com.uzhnu.availabilitymonitoring.domain.model.PowerChecker
import com.uzhnu.availabilitymonitoring.domain.model.RegionReport
import com.uzhnu.availabilitymonitoring.domain.model.ReportState
import com.uzhnu.availabilitymonitoring.domain.usecase.ClearUuidUseCase
import com.uzhnu.availabilitymonitoring.domain.usecase.GetUuidUseCase
import com.uzhnu.availabilitymonitoring.presentation.service.permissions.TAG_PERMISSIONS
import com.uzhnu.availabilitymonitoring.presentation.service.permissions.getPermissionsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val application: Application,
    private val powerCheckerAlarm: PowerChecker,
    private val uuidUseCase: GetUuidUseCase,
    private val clearUuidUseCase: ClearUuidUseCase,
    private val applicationLogger: ApplicationLogger,
    private val sendLogsUseCase: SendLogFilesUseCaseImpl,
    private val generateLogUseCase: GenerateReportUseCaseImpl
) : AndroidViewModel(application) {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(
        HomeUiState.LoadingState()
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        initialiseViewModel()
    }

    private fun initialiseViewModel() {
        startPowerCheckUp()
        checkUser()
    }

    private fun startPowerCheckUp() {
        powerCheckerAlarm.startCheckup()
    }

    private fun checkUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val newUiState = when (val uuid = uuidUseCase.getExistingUuid()) {
                is ExistingUuidState.CorrectUuid -> {
                    launch {
                        sendLogs(uuid.uuid)
                    }
                    HomeUiState.SuccessState(uuid = uuid.uuid)
                }
                is ExistingUuidState.EmptyUuid -> {
                    HomeUiState.NavigateUuidState()
                }
                is ExistingUuidState.UnknownError -> {
                    HomeUiState.ErrorState()
                }
            }
            _uiState.update { newUiState }
        }
    }

    private suspend fun sendLogs(uuid: String) {
        applicationLogger.log(TAG_PERMISSIONS, getPermissionsState(application))
        generateLogUseCase.generateAndSaveReport(RegionReport.WEEK, emptyList())
    }

    //Change Uuid area
    fun onChangeUuidClick() {
        showDialogChangeUuid(true)
    }

    fun onChangeUuidConfirmed() {
        viewModelScope.launch(Dispatchers.IO) {
            clearUuidUseCase.invoke(uiState.value.uuid)
            _uiState.update { HomeUiState.NavigateUuidState() }
        }
    }

    fun onChangeUuidCancelled() {
        showDialogChangeUuid(false)
    }

    fun onSendLogsClick() = viewModelScope.launch(Dispatchers.IO) {
//        _uiState.update { HomeUiState.LoadingState(isLoading = true, uuid = it.uuid) }

        when (generateLogUseCase.generateReport(RegionReport.WEEK, emptyList())){
            is GenerateReportState.CorrectUuid -> TODO()
            ReportState.EmptyResult -> TODO()
            GenerateReportState.EmptyUuid -> TODO()
            GenerateReportState.NoConnection -> TODO()
            is ReportState.NoError -> TODO()
            GenerateReportState.NoInternet -> TODO()
            GenerateReportState.NotValidUuid -> TODO()
            is ReportState.SuccessResult -> {
                
            }
            is GenerateReportState.UnknownError -> TODO()
            is ReportState.UnknownError -> TODO()
        }
//        _uiState.value.uuid?.let { uuid ->
           Log.d("onSendLogsClick",generateLogUseCase.generateReport(RegionReport.WEEK, emptyList()).toString())
//                .onSuccess {
//                    _uiState.update { HomeUiState.SuccessState(uuid = it.uuid) }
//                }.onFailure {
//                    _uiState.update { HomeUiState.ErrorState(uuid = it.uuid) }
//                }
//        }
    }



    private fun showDialogChangeUuid(shouldShowDialog: Boolean) {
        val currentUIState = _uiState.value as? HomeUiState.SuccessState
        currentUIState?.let {
            _uiState.update {
                currentUIState.copy(shouldShowChangeUuidDialog = shouldShowDialog)
            }
        }
    }
}

sealed interface HomeUiState {
    val uuid: String?
    val isLoading: Boolean

    data class LoadingState(
        override val isLoading: Boolean = true,
        override val uuid: String? = null
    ) : HomeUiState

    data class ErrorState(
        override val isLoading: Boolean = false,
        override val uuid: String? = null
    ) : HomeUiState

    data class SuccessState(
        override val isLoading: Boolean = false,
        override val uuid: String?,
        val shouldShowChangeUuidDialog: Boolean = false
    ) : HomeUiState

    data class NavigateUuidState(
        override val isLoading: Boolean = false,
        override val uuid: String? = null
    ) : HomeUiState
}
