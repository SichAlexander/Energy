package com.uzhnu.availabilitymonitoring.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.uzhnu.availabilitymonitoring.presentation.ui.navigation.AvailabilityMonitoringNavigationActions
import com.uzhnu.availabilitymonitoring.presentation.ui.navigation.SetupNavGraph
import com.uzhnu.availabilitymonitoring.presentation.ui.theme.AvailabilityMonitoringTheme

@Composable
fun AvailabilityMonitoringApp() {
    AvailabilityMonitoringTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            AvailabilityMonitoringNavigationActions(navController)
        }

        SetupNavGraph(navController, navigationActions)
    }
}
