package com.uzhnu.availabilitymonitoring.data.logging

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApplicationLoggerModule {

    @Singleton
    @Provides
    fun provideLogger(@ApplicationContext appContext: Context): ApplicationLogger {
        return ApplicationLogger(appContext)
    }
}
