package com.mr3y.podcaster.ui.screens

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mr3y.podcaster.BuildConfig
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.ui.components.plus
import com.mr3y.podcaster.ui.presenter.Theme
import com.mr3y.podcaster.ui.presenter.UserPreferences
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.isAppThemeDark
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight

@Composable
fun SettingsScreen(
    userPreferences: UserPreferences,
    externalContentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    onNavigateUp: () -> Unit,
    onLicensesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedTheme by userPreferences.selectedTheme.collectAsStateWithLifecycle()
    val isDynamicColorOn by userPreferences.dynamicColorEnabled.collectAsStateWithLifecycle()
    val urlHandler = LocalUriHandler.current
    SettingsScreen(
        selectedTheme = selectedTheme ?: Theme.SystemDefault,
        onSelectingTheme = userPreferences::setAppTheme,
        isDynamicColorsOn = isDynamicColorOn,
        onToggleDynamicColor = { setEnabled ->
            if (setEnabled) {
                userPreferences.enableDynamicColor()
            } else {
                userPreferences.disableDynamicColor()
            }
        },
        onNavigateUp = onNavigateUp,
        onLicensesClick = onLicensesClick,
        onFeedbackClick = {
            urlHandler.openUri("https://github.com/mr3y-the-programmer/Podcaster/issues/new?template=bug_report.md")
        },
        onPrivacyPolicyClick = {
            urlHandler.openUri("https://mr3y-the-programmer.github.io/Podcaster/docs/PrivacyPolicy")
        },
        externalContentPadding = externalContentPadding,
        excludedWindowInsets = excludedWindowInsets,
        modifier = modifier,
    )
}

@Composable
fun SettingsScreen(
    selectedTheme: Theme,
    onSelectingTheme: (Theme) -> Unit,
    isDynamicColorsOn: Boolean,
    onToggleDynamicColor: (Boolean) -> Unit,
    onNavigateUp: () -> Unit,
    onLicensesClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    externalContentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
) {
    val isDarkTheme = isAppThemeDark()
    val context = LocalContext.current
    LaunchedEffect(key1 = isDarkTheme) {
        context.setStatusBarAppearanceLight(isAppearanceLight = !isDarkTheme)
    }
    Scaffold(
        topBar = { SettingsTopAppBar(onNavigateUp = onNavigateUp) },
        contentWindowInsets = if (excludedWindowInsets != null) ScaffoldDefaults.contentWindowInsets.exclude(excludedWindowInsets) else ScaffoldDefaults.contentWindowInsets,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(contentPadding + externalContentPadding)
                .fillMaxSize(),
        ) {
            val strings = LocalStrings.current
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                AppearanceSection(
                    selectedTheme,
                    onSelectingTheme,
                    isDynamicColorsOn,
                    onToggleDynamicColor,
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                SettingsTextButton(
                    text = strings.open_source_licenses_label,
                    onClick = onLicensesClick,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                Heading(
                    text = strings.version_label,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                )
                Text(
                    text = BuildConfig.VERSION_NAME,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                SettingsTextButton(
                    text = strings.feedback_and_issues_label,
                    onClick = onFeedbackClick,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                SettingsTextButton(
                    text = strings.privacy_policy_label,
                    onClick = onPrivacyPolicyClick,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                )
            }

            Text(
                text = strings.powered_by_label,
                color = MaterialTheme.colorScheme.inverseSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onSurface),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        title = {
            Text(
                text = strings.settings_label,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
            )
        },
        actions = {},
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun AppearanceSection(
    selectedTheme: Theme,
    onSelectingTheme: (Theme) -> Unit,
    isDynamicColorsOn: Boolean,
    onToggleDynamicColor: (Boolean) -> Unit,
) {
    val strings = LocalStrings.current
    Heading(
        text = strings.appearance_label,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
    )
    SubHeading(
        text = strings.theme_heading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 24.dp),
    )
    Theme.entries.forEach { theme ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClickLabel = null,
                    role = Role.RadioButton,
                    onClick = {
                        if (theme != selectedTheme) {
                            onSelectingTheme(theme)
                        }
                    },
                ),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            RadioButton(
                selected = theme == selectedTheme,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primaryTertiary,
                ),
                modifier = Modifier.size(48.dp),
            )
            val themeLabel = when (theme) {
                Theme.Light -> strings.theme_light_label
                Theme.Dark -> strings.theme_dark_label
                Theme.SystemDefault -> strings.theme_system_default_label
            }
            Text(
                text = themeLabel,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
            )
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        SubHeading(
            text = strings.dynamic_colors_label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 24.dp),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) {
            Checkbox(
                checked = isDynamicColorsOn,
                onCheckedChange = onToggleDynamicColor,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primaryTertiary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimaryTertiary,
                ),
            )
            Text(
                text = if (isDynamicColorsOn) strings.dynamic_colors_on_label else strings.dynamic_colors_off_label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun SettingsTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClickLabel = null,
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Heading(
            text = text,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Heading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

@Composable
private fun SubHeading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

@PodcasterPreview
@Composable
fun SettingsScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        SettingsScreen(
            Theme.SystemDefault,
            { _ -> },
            true,
            { _ -> },
            {},
            {},
            {},
            {},
            PaddingValues(0.dp),
            null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
