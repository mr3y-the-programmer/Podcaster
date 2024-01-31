package com.mr3y.podcaster.ui.theme

import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.view.WindowInsetsControllerCompat

fun Context.setStatusBarAppearanceLight(
    isAppearanceLight: Boolean,
) {
    if (this !is ComponentActivity) return

    val window = this.window
    if (Build.VERSION.SDK_INT >= 29) {
        window.isStatusBarContrastEnforced = false
    }

    WindowInsetsControllerCompat(window, window.decorView).run {
        isAppearanceLightStatusBars = isAppearanceLight
    }
}

fun Context.isStatusBarAppearanceLight(): Boolean {
    this as ComponentActivity

    val window = this.window
    return WindowInsetsControllerCompat(window, window.decorView).run { isAppearanceLightStatusBars }
}
