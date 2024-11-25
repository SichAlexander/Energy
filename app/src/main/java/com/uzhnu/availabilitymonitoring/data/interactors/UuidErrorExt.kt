package com.uzhnu.availabilitymonitoring.data.interactors

import com.uzhnu.availabilitymonitoring.data.network.interceptor.NoConnectionInterceptor
import com.uzhnu.availabilitymonitoring.domain.model.UuidState

internal fun Throwable.checkInternetConnection(): UuidState {
    return when (this) {
        is NoConnectionInterceptor.NoConnectivityException -> {
            UuidState.NoConnection
        }
        is NoConnectionInterceptor.NoInternetException -> {
            UuidState.NoInternet
        }
        else -> {
            UuidState.UnknownError(this)
        }
    }
}
