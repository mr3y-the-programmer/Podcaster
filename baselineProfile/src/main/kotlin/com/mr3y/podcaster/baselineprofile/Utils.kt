package com.mr3y.podcaster.baselineprofile

import android.Manifest
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.benchmark.macro.MacrobenchmarkScope

internal fun MacrobenchmarkScope.allowNotifications() {
    if (SDK_INT >= TIRAMISU) {
        val command = "pm grant $packageName ${Manifest.permission.POST_NOTIFICATIONS}"
        device.executeShellCommand(command)
    }
}
