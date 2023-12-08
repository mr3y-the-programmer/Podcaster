package com.mr3y.podcaster

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import com.mr3y.podcaster.ui.screens.HomeScreen
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goEdgeToEdge()

        setContent {
            val useDarkIcons = !isSystemInDarkTheme()

            DisposableEffect(useDarkIcons) {
                goEdgeToEdge(isDarkTheme = { !useDarkIcons })

                onDispose {}
            }
            PodcasterTheme {
                HomeScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    private fun goEdgeToEdge(
        isDarkTheme: (Resources) -> Boolean = { resources ->
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
    ) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
                detectDarkMode = isDarkTheme
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b),
                detectDarkMode = isDarkTheme
            )
        )
    }
}
