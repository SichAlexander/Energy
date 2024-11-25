package com.uzhnu.availabilitymonitoring.presentation.ui.screen.uuidinput

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uzhnu.availabilitymonitoring.R
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.regularPadding
import com.uzhnu.availabilitymonitoring.presentation.ui.view.common.AppProgressBar
import com.uzhnu.availabilitymonitoring.presentation.ui.view.common.ApplicationTopBar
import com.uzhnu.availabilitymonitoring.presentation.ui.view.common.error.ErrorAlertDialog
import com.uzhnu.availabilitymonitoring.presentation.ui.view.uuidfield.StatefulUUIDField

@Composable
fun UUIDInputScreen(
    modifier: Modifier = Modifier,
    viewModel: UUIDInputScreenViewModel = hiltViewModel(),
    onHomeNavigate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isNavigateHome) {
        onHomeNavigate()
        return
    }

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ApplicationTopBar()
            UUIDInputScreenContent(
                uiState = uiState,
                onUUIDInputChange = { viewModel.onUUIDInputChanged(it) },
                onSubmitClick = { viewModel.onUUIDSubmitClick() },
                onErrorDialogClick = { viewModel.onErrorDialogConfirmClick() })
        }
    }
}

@Composable
fun UUIDInputScreenContent(
    modifier: Modifier = Modifier,
    uiState: UUIDInputUIState,
    onUUIDInputChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onErrorDialogClick: () -> Unit
) {
    Box(modifier = modifier) {
        Column(Modifier.padding(horizontal = regularPadding, vertical = regularPadding)) {
            StatefulUUIDField(
                viewState = uiState.uuidFieldViewState,
                onTextChange = onUUIDInputChange,
                onSubmitClick = onSubmitClick
            )
            Spacer(modifier = Modifier.height(regularPadding))
            if (uiState.isConnectionError) {
                Text(
                    text = stringResource(id = R.string.uuid_error_no_connection),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        if (uiState.isLoading) {
            AppProgressBar()
        }

        if (uiState.shouldShowErrorDialog) {
            ErrorAlertDialog(
                onErrorDialogClick
            )
        }
    }
}
