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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.Theme
import com.mr3y.podcaster.ui.presenter.UserPreferences
import com.mr3y.podcaster.ui.screens.HomeScreen
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var podcasterAppState: PodcasterAppState

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            userPreferences.selectedTheme.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED).collect { theme ->
                splashScreen.setKeepOnScreenCondition { theme == null }
            }
        }

        goEdgeToEdge()

        setContent {
            val selectedTheme by userPreferences.selectedTheme.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.CREATED)
            val isDynamicColorOn by userPreferences.dynamicColorEnabled.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.CREATED)
            val useDarkIcons = shouldUseDarkIcons(theme = selectedTheme)

            DisposableEffect(useDarkIcons) {
                goEdgeToEdge(isDarkTheme = { !useDarkIcons })

                onDispose {}
            }
            if (selectedTheme != null) {
                when (selectedTheme!!) {
                    Theme.Light -> {
                        goEdgeToEdge(isDarkTheme = { false })
                    }
                    Theme.Dark -> {
                        goEdgeToEdge(isDarkTheme = { true })
                    }
                    Theme.SystemDefault -> {
                        val isDarkTheme = isSystemInDarkTheme()
                        goEdgeToEdge(isDarkTheme = { isDarkTheme })
                    }
                }
                PodcasterTheme(
                    theme = selectedTheme!!,
                    dynamicColor = isDynamicColorOn,
                ) {
                    HomeScreen(
                        appState = podcasterAppState,
                        userPreferences = userPreferences,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    private fun goEdgeToEdge(
        isDarkTheme: (Resources) -> Boolean = { resources ->
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        },
    ) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
                detectDarkMode = isDarkTheme,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b),
                detectDarkMode = isDarkTheme,
            ),
        )
    }

    @Composable
    private fun shouldUseDarkIcons(theme: Theme?): Boolean {
        if (theme == null) {
            return !isSystemInDarkTheme()
        }

        return when (theme) {
            Theme.Light -> true
            Theme.Dark -> false
            Theme.SystemDefault -> !isSystemInDarkTheme()
        }
    }
}
