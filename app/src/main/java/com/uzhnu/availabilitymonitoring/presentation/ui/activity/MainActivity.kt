package com.uzhnu.availabilitymonitoring.presentation.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.uzhnu.availabilitymonitoring.presentation.ui.AvailabilityMonitoringApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AvailabilityMonitoringApp()
        }
    }
}
