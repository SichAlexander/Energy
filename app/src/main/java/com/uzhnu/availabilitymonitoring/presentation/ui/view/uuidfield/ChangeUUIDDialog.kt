package com.uzhnu.availabilitymonitoring.presentation.ui.view.uuidfield

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.uzhnu.availabilitymonitoring.R
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.AvailabilityMonitoringTheme

@Composable
fun ChangeUuidDialog(
    onChangeUuidApproved: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(R.string.home_uuid_change_confirmation)) },
        text = { Text(stringResource(R.string.home_uuid_change_information, 0)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDismissClick) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onChangeUuidApproved) {
                Text(text = stringResource(R.string.yes))
            }
        }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ChangeUUIDDialogPreview() {
    AvailabilityMonitoringTheme {
        ChangeUuidDialog(
            onChangeUuidApproved = {}, onDismissClick = {})
    }
}
