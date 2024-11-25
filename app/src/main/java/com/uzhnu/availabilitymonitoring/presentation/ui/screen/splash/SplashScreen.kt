package com.uzhnu.availabilitymonitoring.presentation.ui.screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationState
import com.uzhnu.availabilitymonitoring.R
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.largePadding
import com.uzhnu.availabilitymonitoring.presentation.ui.view.common.error.ErrorAnimation

@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit,
    onNavigateUuidInput: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SplashScreenContent(
        uiState = uiState,
        onNavigateHome = onNavigateHome,
        onNavigateUuidInput = onNavigateUuidInput,
        onAnimationFinished = { viewModel.onAnimationFinished() }
    )
}

@Composable
fun SplashScreenContent(
    uiState: SplashUIState,
    onNavigateHome: () -> Unit,
    onNavigateUuidInput: () -> Unit,
    onAnimationFinished: () -> Unit

) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logo))
    val logoAnimationState = animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    if (uiState != SplashUIState.Failure) {
        SplashScreenAnimation(
            onAnimationFinished,
            composition,
            logoAnimationState
        )
    }

    when (uiState) {
        SplashUIState.Loading -> {}
        SplashUIState.NavigateHome -> onNavigateHome()
        SplashUIState.NavigateUuidInput -> onNavigateUuidInput()
        SplashUIState.Failure -> ErrorAnimation()
    }
}

@Composable
fun SplashScreenAnimation(
    onAnimationFinished: () -> Unit,
    composition: LottieComposition?,
    animationState: LottieAnimationState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(largePadding)
    ) {

        LottieAnimation(
            composition = composition,
            progress = { animationState.progress }
        )

        val process = animationState.progress
        val isAnimationFinished = process > END_ANIMATION
        if (isAnimationFinished) {
            onAnimationFinished()
        }
    }
}

private const val END_ANIMATION = 0.75f
