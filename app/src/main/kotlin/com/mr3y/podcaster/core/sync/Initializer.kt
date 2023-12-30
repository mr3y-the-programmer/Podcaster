package com.mr3y.podcaster.core.sync

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager

fun initializeWorkManagerInstance(appContext: Context): WorkManager {
    return WorkManager.getInstance(appContext).apply {
        enqueueUniquePeriodicWork(
            SubscriptionsSyncWorker.PeriodicWorkRequestID,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            SubscriptionsSyncWorker.subscriptionsPeriodicSyncWorker()
        )
    }
}
