package com.mr3y.podcaster

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.mr3y.podcaster.core.credentials_provider.CredentialsProvider
import com.mr3y.podcaster.core.opml.FileManager
import com.mr3y.podcaster.core.sync.initializeWorkManagerInstance
import dagger.hilt.android.HiltAndroidApp
import dev.shreyaspatil.bytemask.android.AndroidBytemask
import javax.inject.Inject

@HiltAndroidApp
class PodcasterApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var fileManager: FileManager

    override fun onCreate() {
        super.onCreate()
        initializeWorkManagerInstance(this)
        AndroidBytemask.init(this)
        CredentialsProvider.setAPICredentials(key = BytemaskConfig.API_KEY, secret = BytemaskConfig.API_SECRET)
        fileManager.registerActivityWatcher()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}
