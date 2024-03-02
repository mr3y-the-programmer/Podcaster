package com.mr3y.podcaster.core.logger.di

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import com.mr3y.podcaster.core.logger.Logger
import com.mr3y.podcaster.core.logger.internal.DefaultLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import co.touchlab.kermit.Logger as KermitLogger

@Module
@InstallIn(SingletonComponent::class)
object LoggingModule {

    @Provides
    @OptIn(ExperimentalKermitApi::class)
    @Singleton
    fun provideLoggerInstance(): Logger {
        return DefaultLogger(
            KermitLogger(config = loggerConfigInit(platformLogWriter(), CrashlyticsLogWriter())),
        )
    }
}
