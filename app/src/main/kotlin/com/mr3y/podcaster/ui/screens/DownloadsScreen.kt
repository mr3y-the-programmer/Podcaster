package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.core.model.EpisodeDownloadStatus
import com.mr3y.podcaster.core.model.EpisodeWithDownloadMetadata
import com.mr3y.podcaster.core.sampledata.EpisodesWithDownloadMetadata
import com.mr3y.podcaster.ui.components.DownloadButton
import com.mr3y.podcaster.ui.components.LoadingIndicator
import com.mr3y.podcaster.ui.components.TopBar
import com.mr3y.podcaster.ui.components.rememberHtmlToAnnotatedString
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.downloads.DownloadsUIState
import com.mr3y.podcaster.ui.presenter.downloads.DownloadsViewModel
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.isAppThemeDark
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight

@Composable
fun DownloadsScreen(
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    onNavDrawerClick: () -> Unit,
    appState: PodcasterAppState,
    contentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
    viewModel: DownloadsViewModel = hiltViewModel(),
) {
    val downloadsState by viewModel.state.collectAsStateWithLifecycle()
    DownloadsScreen(
        state = downloadsState,
        onEpisodeClick = onEpisodeClick,
        onNavDrawerClick = onNavDrawerClick,
        onResumeDownloadingEpisode = appState::resumeDownloading,
        onPauseDownloadingEpisode = appState::pauseDownloading,
        externalContentPadding = contentPadding,
        excludedWindowInsets = excludedWindowInsets,
        modifier = modifier,
    )
}

@Composable
fun DownloadsScreen(
    state: DownloadsUIState,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    onNavDrawerClick: () -> Unit,
    onResumeDownloadingEpisode: (episodeId: Long) -> Unit,
    onPauseDownloadingEpisode: (episodeId: Long) -> Unit,
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
        topBar = {
            TopBar(
                isTopLevelScreen = true,
                onNavIconClick = onNavDrawerClick,
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
        contentWindowInsets = if (excludedWindowInsets != null) ScaffoldDefaults.contentWindowInsets.exclude(excludedWindowInsets) else ScaffoldDefaults.contentWindowInsets,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) { contentPadding ->
        val contentModifier = Modifier
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
            .fillMaxSize()

        if (state.isLoading) {
            LoadingIndicator(modifier = contentModifier)
        } else {
            DownloadsList(
                downloads = state.downloads,
                onEpisodeClick = onEpisodeClick,
                onResumeDownloadingEpisode = onResumeDownloadingEpisode,
                onPauseDownloadingEpisode = onPauseDownloadingEpisode,
                externalContentPadding = externalContentPadding,
                modifier = contentModifier,
            )
        }
    }
}

@Composable
private fun DownloadsList(
    downloads: List<EpisodeWithDownloadMetadata>,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    onResumeDownloadingEpisode: (episodeId: Long) -> Unit,
    onPauseDownloadingEpisode: (episodeId: Long) -> Unit,
    externalContentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    if (downloads.isEmpty()) {
        EmptyDownloads(modifier)
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = externalContentPadding,
        ) {
            itemsIndexed(
                downloads,
                key = { _, (episode, _) -> episode.id },
            ) { index, (episode, downloadMetadata) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onEpisodeClick(episode.id, episode.artworkUrl) })
                        .padding(vertical = 8.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(72.dp),
                    ) {
                        AsyncImage(
                            model = episode.artworkUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.FillBounds,
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = episode.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = rememberHtmlToAnnotatedString(text = episode.description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        DownloadButton(
                            downloadMetadata = downloadMetadata,
                            onDownload = { },
                            onResumingDownload = { onResumeDownloadingEpisode(episode.id) },
                            onPausingDownload = { onPauseDownloadingEpisode(episode.id) },
                        )
                    }
                }
                if (index != downloads.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun EmptyDownloads(
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Text(
            text = strings.downloads_empty_list,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@PodcasterPreview
@Composable
fun DownloadsScreenLoadingPreview() {
    PodcasterTheme(dynamicColor = false) {
        val state by remember {
            mutableStateOf(
                DownloadsUIState(
                    isLoading = true,
                    downloads = emptyList(),
                ),
            )
        }
        DownloadsScreen(
            state = state,
            onEpisodeClick = { _, _ -> },
            onNavDrawerClick = { /*TODO*/ },
            onResumeDownloadingEpisode = {},
            onPauseDownloadingEpisode = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@PodcasterPreview
@Composable
fun DownloadsScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        val state by remember {
            mutableStateOf(
                DownloadsUIState(
                    isLoading = false,
                    downloads = EpisodesWithDownloadMetadata.filterNot { it.downloadMetadata.downloadStatus == EpisodeDownloadStatus.NotDownloaded },
                ),
            )
        }
        DownloadsScreen(
            state = state,
            onEpisodeClick = { _, _ -> },
            onNavDrawerClick = { /*TODO*/ },
            onResumeDownloadingEpisode = {},
            onPauseDownloadingEpisode = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@PodcasterPreview
@Composable
fun DownloadsScreenEmptyDownloadsPreview() {
    PodcasterTheme(dynamicColor = false) {
        val state by remember {
            mutableStateOf(
                DownloadsUIState(
                    isLoading = false,
                    downloads = emptyList(),
                ),
            )
        }
        DownloadsScreen(
            state = state,
            onEpisodeClick = { _, _ -> },
            onNavDrawerClick = { /*TODO*/ },
            onResumeDownloadingEpisode = {},
            onPauseDownloadingEpisode = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
