package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.core.opml.model.OpmlResult
import com.mr3y.podcaster.ui.components.LoadingIndicator
import com.mr3y.podcaster.ui.components.TopBar
import com.mr3y.podcaster.ui.components.plus
import com.mr3y.podcaster.ui.presenter.opml.OpmlViewModel
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.isAppThemeDark
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight

@Composable
fun ImportExportScreen(
    onNavigateUp: () -> Unit,
    contentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
    viewModel: OpmlViewModel = hiltViewModel(),
) {
    val result by viewModel.result.collectAsStateWithLifecycle()
    ImportExportScreen(
        result = result,
        onNavigateUp = onNavigateUp,
        onImporting = viewModel::import,
        onExporting = viewModel::export,
        onConsumeResult = viewModel::consumeResult,
        externalContentPadding = contentPadding,
        excludedWindowInsets = excludedWindowInsets,
        modifier = modifier,
    )
}

@Composable
fun ImportExportScreen(
    result: OpmlResult,
    onNavigateUp: () -> Unit,
    onImporting: () -> Unit,
    onExporting: () -> Unit,
    onConsumeResult: () -> Unit,
    externalContentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val isDarkTheme = isAppThemeDark()
    val context = LocalContext.current
    LaunchedEffect(key1 = isDarkTheme) {
        context.setStatusBarAppearanceLight(isAppearanceLight = !isDarkTheme)
    }
    Scaffold(
        topBar = {
            TopBar(
                onUpButtonClick = onNavigateUp,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(end = 16.dp),
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState, Modifier.padding(externalContentPadding)) },
        contentWindowInsets = if (excludedWindowInsets != null) ScaffoldDefaults.contentWindowInsets.exclude(excludedWindowInsets) else ScaffoldDefaults.contentWindowInsets,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) { contentPadding ->
        val strings = LocalStrings.current
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding + externalContentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 56.dp),
        ) {
            ImportOrExportButton(label = strings.import_label, onClick = onImporting, modifier = Modifier.fillMaxWidth())
            ImportOrExportButton(label = strings.export_label, onClick = onExporting, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = strings.import_notice,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (result is OpmlResult.Loading) {
                LoadingIndicator(modifier = Modifier.fillMaxWidth())
            }
            LaunchedEffect(result) {
                if (result is OpmlResult.Error || result is OpmlResult.Success) {
                    val message = when (result) {
                        is OpmlResult.Success -> strings.import_succeeded
                        is OpmlResult.Error.NoContentInOpmlFile -> strings.import_empty_file_error
                        is OpmlResult.Error.NetworkError -> strings.import_network_error
                        is OpmlResult.Error.EncodingError, is OpmlResult.Error.DecodingError -> strings.import_corrupted_file_error
                        is OpmlResult.Error.UnknownFailure -> strings.import_unknown_error
                        else -> "" // Not reachable, but when can't infer that yet (see https://youtrack.jetbrains.com/issue/KT-8781/Consider-making-smart-casts-smart-enough-to-handle-exhaustive-value-sets)
                    }
                    snackBarHostState.showSnackbar(message)
                    onConsumeResult()
                }
            }
        }
    }
}

@Composable
private fun ImportOrExportButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryTertiary,
            contentColor = MaterialTheme.colorScheme.onPrimaryTertiary,
        ),
        modifier = modifier,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@PodcasterPreview
@Composable
fun ImportExportScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        ImportExportScreen(
            result = OpmlResult.Idle,
            onNavigateUp = {},
            onImporting = { },
            onExporting = { },
            onConsumeResult = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
