package com.uzhnu.availabilitymonitoring.presentation.service.notification

import android.content.Context
import com.uzhnu.availabilitymonitoring.data.logging.ApplicationLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NotificationModule {

    @Singleton
    @Provides
    fun providePowerCheckerNotification(
        @ApplicationContext appContext: Context,
        applicationLogger: ApplicationLogger
    ): PowerCheckerNotifications {
        return PowerCheckerNotifications(appContext, applicationLogger)
    }
}
