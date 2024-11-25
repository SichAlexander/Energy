package com.uzhnu.availabilitymonitoring.presentation.ui.view.common.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.uzhnu.availabilitymonitoring.R
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.AvailabilityMonitoringTheme
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.largePadding
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.regularPadding

@Composable
fun ErrorAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        LottieAnimation(
            modifier = Modifier.padding(vertical = regularPadding, horizontal = largePadding),
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        Text(
            text = stringResource(id = R.string.unknown_error_message),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = regularPadding, horizontal = largePadding),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable

fun ErrorStatePreview() {
    AvailabilityMonitoringTheme {
        ErrorAnimation()
    }
}
