package com.uzhnu.availabilitymonitoring.presentation.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.uzhnu.availabilitymonitoring.presentation.ui.screen.home.HomeScreen
import com.uzhnu.availabilitymonitoring.presentation.ui.screen.splash.SplashScreen
import com.uzhnu.availabilitymonitoring.presentation.ui.screen.uuidinput.UUIDInputScreen

sealed class AvailabilityMonitoringDestinations(val route: String) {
    object Splash : AvailabilityMonitoringDestinations("splash_screen")
    object Home : AvailabilityMonitoringDestinations("home_screen")
    object UuidInput : AvailabilityMonitoringDestinations("uuid_input_screen")
}

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    actions: AvailabilityMonitoringNavigationActions
) {
    NavHost(
        navController = navController,
        startDestination = AvailabilityMonitoringDestinations.Splash.route
    ) {
        composable(route = AvailabilityMonitoringDestinations.Splash.route) {
            SplashScreen(
                onNavigateHome = actions.navigateToHome,
                onNavigateUuidInput = actions.navigateToUuidInput
            )
        }
        composable(route = AvailabilityMonitoringDestinations.Home.route) {
            BackHandler(true) {}
            HomeScreen(onNavigateUuidInput = actions.navigateToUuidInput)
        }
        composable(route = AvailabilityMonitoringDestinations.UuidInput.route) {
            BackHandler(true) {}
            UUIDInputScreen(onHomeNavigate = actions.navigateToHome)
        }
    }
}

class AvailabilityMonitoringNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(AvailabilityMonitoringDestinations.Home.route) {
            popUpTo(AvailabilityMonitoringDestinations.UuidInput.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    val navigateToUuidInput: () -> Unit = {
        navController.navigate(AvailabilityMonitoringDestinations.UuidInput.route) {
            popUpTo(AvailabilityMonitoringDestinations.Home.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}
