package com.uzhnu.availabilitymonitoring.presentation.ui.view.common.error

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.uzhnu.availabilitymonitoring.R
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.AvailabilityMonitoringTheme
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.regularPadding

@Composable
fun ErrorAlertDialog(onConfirmButtonClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = onConfirmButtonClick) {
                Text(
                    text = stringResource(id = R.string.ok),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_error_outline_24),
                    contentDescription = "Error"
                )
                Spacer(modifier = Modifier.height(regularPadding))
                Text(
                    text = stringResource(id = R.string.unknown_error_message),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ErrorDialogPreview() {
    AvailabilityMonitoringTheme {
        ErrorAlertDialog {}
    }
}
