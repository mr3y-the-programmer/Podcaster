package com.mr3y.podcaster.core.sync

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo

private const val SYNC_NOTIFICATION_ID = 10
private const val SYNC_NOTIFICATION_CHANNEL_ID = "SyncNotificationChannel"

/**
 * Foreground information when sync workers are being run with a foreground service
 */
fun Context.syncForegroundInfo(): ForegroundInfo {
    return if (Build.VERSION.SDK_INT >= 34) {
        ForegroundInfo(SYNC_NOTIFICATION_ID, syncWorkNotification(), FOREGROUND_SERVICE_TYPE_SHORT_SERVICE)
    } else {
        ForegroundInfo(SYNC_NOTIFICATION_ID, syncWorkNotification())
    }
}

/**
 * Notification displayed when sync workers are being run with a foreground service
 */
private fun Context.syncWorkNotification(): Notification {
    val channel = NotificationChannel(
        SYNC_NOTIFICATION_CHANNEL_ID,
        getString(R.string.sync_work_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.sync_work_notification_channel_description)
    }
    // Register the channel with the system
    val notificationManager: NotificationManager? =
        getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    notificationManager?.createNotificationChannel(channel)

    return NotificationCompat.Builder(this, SYNC_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(getString(R.string.sync_work_notification_title))
        .setContentText(getString(R.string.sync_work_notification_body))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}
