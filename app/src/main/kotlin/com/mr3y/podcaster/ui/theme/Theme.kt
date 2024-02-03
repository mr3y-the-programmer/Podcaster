package com.mr3y.podcaster.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.mr3y.podcaster.ProvideStrings
import com.mr3y.podcaster.ui.presenter.Theme

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

val ColorScheme.primaryTertiary
    @Composable
    @ReadOnlyComposable
    get() = if (isAppThemeLight()) primary else tertiary

val ColorScheme.onPrimaryTertiary
    @Composable
    @ReadOnlyComposable
    get() = if (isAppThemeLight()) onPrimary else onTertiary

val ColorScheme.tertiaryPrimary
    @Composable
    @ReadOnlyComposable
    get() = if (isAppThemeLight()) tertiary else primary

val ColorScheme.onTertiaryPrimary
    @Composable
    @ReadOnlyComposable
    get() = if (isAppThemeLight()) onTertiary else onPrimary

val ColorScheme.primaryTertiaryContainer
    @Composable
    @ReadOnlyComposable
    get() = if (isAppThemeLight()) primaryContainer else tertiaryContainer

val ColorScheme.onPrimaryTertiaryContainer
    @Composable
    @ReadOnlyComposable
    get() = if (isAppThemeLight()) onPrimaryContainer else onTertiaryContainer

val LocalAppTheme = staticCompositionLocalOf<Theme> {
    error("AppTheme not provided")
}

/**
 * Returns whether the app's color theme is a light one.
 */
@Composable
@ReadOnlyComposable
fun isAppThemeLight(): Boolean {
    return LocalAppTheme.current == Theme.Light || (LocalAppTheme.current == Theme.SystemDefault && !isSystemInDarkTheme())
}

/**
 * Returns whether the app's color theme is a dark one.
 */
@Composable
@ReadOnlyComposable
fun isAppThemeDark(): Boolean {
    return LocalAppTheme.current == Theme.Dark || (LocalAppTheme.current == Theme.SystemDefault && isSystemInDarkTheme())
}

@Composable
fun PodcasterTheme(
    theme: Theme = Theme.SystemDefault,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (theme) {
        Theme.Light -> false
        Theme.Dark -> true
        Theme.SystemDefault -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    ProvideStrings {
        CompositionLocalProvider(LocalAppTheme provides theme) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                shapes = Shapes,
                content = content,
            )
        }
    }
}
