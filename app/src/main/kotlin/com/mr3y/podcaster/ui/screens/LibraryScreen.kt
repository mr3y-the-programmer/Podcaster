package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.ui.components.TopBar
import com.mr3y.podcaster.ui.components.plus
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.isAppThemeDark
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight

@Composable
fun LibraryScreen(
    onDownloadsClick: () -> Unit,
    externalContentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
) {
    val isDarkTheme = isAppThemeDark()
    val context = LocalContext.current
    LaunchedEffect(key1 = isDarkTheme) {
        context.setStatusBarAppearanceLight(isAppearanceLight = !isDarkTheme)
    }
    val strings = LocalStrings.current
    Scaffold(
        topBar = {
            TopBar(
                onUpButtonClick = null,
                title = {
                    Text(
                        text = strings.library_label,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        contentWindowInsets = if (excludedWindowInsets != null) ScaffoldDefaults.contentWindowInsets.exclude(excludedWindowInsets) else ScaffoldDefaults.contentWindowInsets,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding + externalContentPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = null,
                        role = Role.Button,
                        onClick = onDownloadsClick,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.FileDownload,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.downloads_label,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        }
    }
}

@PodcasterPreview
@Composable
fun LibraryScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        LibraryScreen(
            onDownloadsClick = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}
