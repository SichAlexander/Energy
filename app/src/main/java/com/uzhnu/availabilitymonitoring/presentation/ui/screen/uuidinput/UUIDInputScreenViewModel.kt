package com.uzhnu.availabilitymonitoring.presentation.ui.screen.uuidinput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzhnu.availabilitymonitoring.domain.model.UuidState
import com.uzhnu.availabilitymonitoring.domain.usecase.CheckUserUseCase
import com.uzhnu.availabilitymonitoring.presentation.ui.view.uuidfield.UUIDFieldErrors
import com.uzhnu.availabilitymonitoring.presentation.ui.view.uuidfield.UUIDFieldViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UUIDInputScreenViewModel @Inject constructor(private val userChecker: CheckUserUseCase) :
    ViewModel() {

    private val _uiState: MutableStateFlow<UUIDInputUIState> = MutableStateFlow(
        UUIDInputUIState(
            uuidFieldViewState = UUIDFieldViewState(),
            isLoading = false
        )
    )
    val uiState: StateFlow<UUIDInputUIState> = _uiState.asStateFlow()

    fun onUUIDInputChanged(uuidInput: String) {
        _uiState.update {
            it.copy(uuidFieldViewState = UUIDFieldViewState(uuidInput))
        }
    }

    fun onErrorDialogConfirmClick() {
        _uiState.update {
            it.copy(shouldShowErrorDialog = false)
        }
    }

    fun onUUIDSubmitClick() {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch(Dispatchers.IO) {
            val uuid = getUuid().replace(" ", "")

            if(uuid.isEmpty()){
                processUuidInputErrorState(error = UUIDFieldErrors.NotValidId)
                return@launch
            }

            if (uuid.contains(HTTP_CHARS)) {
                processUuidInputErrorState(error = UUIDFieldErrors.LinkEntered)
                return@launch
            }

            processUuidCheck(userChecker.validateUuid(uuid))
        }
    }

    private fun processUuidInputErrorState(error: UUIDFieldErrors){
        _uiState.update {
            val filedUiState =
                it.uuidFieldViewState.copy(error = error)
            it.copy(uuidFieldViewState = filedUiState, isLoading = false)
        }
    }

    private fun processUuidCheck(uuidCheck: UuidState) {
        _uiState.update {
            when (uuidCheck) {
                is UuidState.CorrectUuid -> {
                    val filedUiState = it.uuidFieldViewState.copy(error = null)
                    UUIDInputUIState(filedUiState, isNavigateHome = true)
                }

                is UuidState.NotValidUuid, UuidState.EmptyUuid -> {
                    val filedUiState =
                        it.uuidFieldViewState.copy(error = UUIDFieldErrors.NotValidId)
                    UUIDInputUIState(filedUiState)
                }

                is UuidState.UnknownError -> {
                    it.copy(
                        shouldShowErrorDialog = true,
                        isLoading = false,
                        isConnectionError = false
                    )
                }

                is UuidState.NoInternet, UuidState.NoConnection -> {
                    it.copy(isConnectionError = true, isLoading = false)
                }
            }
        }
    }

    private fun getUuid(): String = _uiState.value.uuidFieldViewState.text

    private companion object {
        const val HTTP_CHARS = "http"
    }
}

data class UUIDInputUIState(
    val uuidFieldViewState: UUIDFieldViewState = UUIDFieldViewState(),
    val isLoading: Boolean = false,
    val isNavigateHome: Boolean = false,
    val isConnectionError: Boolean = false,
    val shouldShowErrorDialog: Boolean = false
)
