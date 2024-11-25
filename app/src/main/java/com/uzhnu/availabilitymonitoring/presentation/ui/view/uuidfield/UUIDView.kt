package com.uzhnu.availabilitymonitoring.presentation.ui.view.uuidfield

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.uzhnu.availabilitymonitoring.R
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.AvailabilityMonitoringTheme
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.largePadding
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.regularPadding
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.smallPadding

@Composable
fun StatefulUUIDField(
    modifier: Modifier = Modifier,
    viewState: UUIDFieldViewState,
    onTextChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
) {
    UUIDFiled(
        modifier = modifier,
        text = viewState.text,
        labelText = stringResource(id = viewState.labelText()),
        isButtonEnabled = viewState.isButtonEnabled(),
        isError = viewState.isError(),
        onTextChange,
        onSubmitClick,

        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UUIDFiled(
    modifier: Modifier = Modifier,
    text: String,
    labelText: String,
    isButtonEnabled: Boolean = true,
    isError: Boolean = false,
    onTextChange: (String) -> Unit,
    onSubmitClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.uuid_input_title),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(regularPadding))
        OutlinedTextField(
            value = text,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onTextChange,
            label = { Text(text = labelText) },
            isError = isError,
            placeholder = { Text(text = stringResource(id = R.string.uuid_hint)) },
            keyboardActions = KeyboardActions(onDone = { onSubmitClick() }),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
        )
        Spacer(modifier = Modifier.height(largePadding))
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onSubmitClick,
            enabled = isButtonEnabled,
        ) {
            Text(
                modifier = Modifier.padding(vertical = smallPadding, horizontal = largePadding),
                text = stringResource(id = R.string.uuid_submit),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DefaultPreview() {
    AvailabilityMonitoringTheme {
        StatefulUUIDField(
            modifier = Modifier.padding(regularPadding),
            viewState = UUIDFieldViewState(text = "fenf-4_fn"),
            onTextChange = {},
            onSubmitClick = {})
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun DefaultPreviewDark() {
    AvailabilityMonitoringTheme {
        StatefulUUIDField(
            modifier = Modifier.padding(regularPadding),
            viewState = UUIDFieldViewState(text = "fenf-4_fn"),
            onTextChange = {},
            onSubmitClick = {})
    }
}
