package com.uzhnu.availabilitymonitoring.presentation.ui.screen.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.uzhnu.availabilitymonitoring.BuildConfig
import com.uzhnu.availabilitymonitoring.R
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.*
import com.uzhnu.availabilitymonitoring.presentation.ui.view.common.AppProgressBar
import com.uzhnu.availabilitymonitoring.presentation.ui.view.common.ApplicationTopBar
import com.uzhnu.availabilitymonitoring.presentation.ui.view.common.error.ErrorAnimation
import com.uzhnu.availabilitymonitoring.presentation.ui.view.common.appColorDynamicBackground
import com.uzhnu.availabilitymonitoring.presentation.ui.view.uuidfield.ChangeUuidDialog
import com.uzhnu.availabilitymonitoring.presentation.ui.view.uuidfield.ZakarpattiaMap

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onNavigateUuidInput: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        ApplicationTopBar()
        HomeScreenContent(
            uiState = uiState,
            onNavigateUuidInput = onNavigateUuidInput,
            onChangeUuidClick = { viewModel.onChangeUuidClick() },
            onChangeUuidApproved = { viewModel.onChangeUuidConfirmed() },
            onChangeUuidCancelled = { viewModel.onChangeUuidCancelled() },
            onSendLogsClick = { viewModel.onSendLogsClick() }
        )
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateUuidInput: () -> Unit,
    onChangeUuidClick: () -> Unit,
    onChangeUuidApproved: () -> Unit,
    onChangeUuidCancelled: () -> Unit,
    onSendLogsClick: () -> Unit,
) {
    when (uiState) {
        is HomeUiState.ErrorState -> ErrorAnimation()
        is HomeUiState.SuccessState -> HomeContent(
            uiState = uiState,
            onChangeUuidClick = onChangeUuidClick,
            onChangeUuidApproved = onChangeUuidApproved,
            onChangeUuidCancelled = onChangeUuidCancelled,
            onSendLogsClick = onSendLogsClick
        )
        is HomeUiState.NavigateUuidState -> onNavigateUuidInput()
        is HomeUiState.LoadingState -> AppProgressBar()
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier, uiState: HomeUiState.SuccessState,
    onChangeUuidClick: () -> Unit,
    onChangeUuidApproved: () -> Unit,
    onChangeUuidCancelled: () -> Unit,
    onSendLogsClick: () -> Unit,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.home))

    Box(modifier = modifier.appColorDynamicBackground()) {
        if (uiState.shouldShowChangeUuidDialog) {
            ChangeUuidDialog(
                onChangeUuidApproved = onChangeUuidApproved,
                onDismissClick = onChangeUuidCancelled
            )
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(largePadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val mockStatuses = mapOf(
                stringResource(id = R.string.Uzhgorod) to 95.0, // 95% електроживлення
                stringResource(id = R.string.Mukachevo) to 95.0, // 95% електроживлення
                stringResource(id = R.string.Mizhhirskiy) to 10.0, // 60% електроживлення
                stringResource(id = R.string.Svalyava) to 40.0, // 60% електроживлення
                stringResource(id = R.string.Tyachiv) to 50.0, // 60% електроживлення
                stringResource(id = R.string.Irshava) to 60.0, // 60% електроживлення
                stringResource(id = R.string.Beregovo) to 70.0, // 60% електроживлення
                stringResource(id = R.string.Khust) to 80.0, // 60% електроживлення
                stringResource(id = R.string.Volovets) to 90.0, // 60% електроживлення
                stringResource(id = R.string.Rahiv) to 50.0, // 60% електроживлення
                stringResource(id = R.string.Vinogradiv) to 20.0, // 60% електроживлення
                stringResource(id = R.string.Bereznuy) to 75.0, // 60% електроживлення
                stringResource(id = R.string.Perechyn) to 30.0  // 30% електроживлення
            )
            uiState.uuid?.let { uuid ->
                Text(
                    text = stringResource(id = R.string.uuid_text, uuid),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = stringResource(id = R.string.current_situation),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            ZakarpattiaMap(modifier = Modifier.weight(1f).padding(start = 60.dp, top = 70.dp),
                mockStatuses,R.drawable.zakarpattia_regions, {})

//            LottieAnimation(
//                modifier = Modifier.weight(1f),
//                composition = composition,
//                iterations = LottieConstants.IterateForever
//            )

            Text(
                text = stringResource(id = R.string.home_message),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(smallPadding))
            ChangeUuidButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onChangeUuidClick = onChangeUuidClick
            )

            if (BuildConfig.DEBUG) {
                SendLogsButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onSendLogsClick = onSendLogsClick
                )
            }
        }
    }
}


@Composable
fun ChangeUuidButton(
    modifier: Modifier = Modifier,
    onChangeUuidClick: () -> Unit
) {
    AppTextButton(
        onClick = onChangeUuidClick,
        modifier = modifier,
        text = R.string.home_uuid_change_message,
        icon = R.drawable.ic_edit
    )
}

@Composable
fun SendLogsButton(
    modifier: Modifier = Modifier,
    onSendLogsClick: () -> Unit
) {
    AppTextButton(
        onClick = onSendLogsClick,
        modifier = modifier,
        text = R.string.home_uuid_send_logs,
        icon = R.drawable.ic_send
    )
}

@Composable
fun AppTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes text: Int,
    @DrawableRes icon: Int
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(buttonTextPadding))
            Text(
                text = stringResource(id = text),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ChangeUUIDDialogPreview() {
    AvailabilityMonitoringTheme {
        HomeContent(
            onChangeUuidApproved = {},
            onChangeUuidCancelled = {},
            onChangeUuidClick = {},
            onSendLogsClick = {},
            uiState = HomeUiState.SuccessState(uuid = "uuid")
        )
    }
}
