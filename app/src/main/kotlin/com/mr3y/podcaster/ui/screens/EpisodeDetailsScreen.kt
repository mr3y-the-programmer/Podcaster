package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Scale
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.core.sampledata.DownloadMetadata
import com.mr3y.podcaster.core.sampledata.EpisodeWithDetails
import com.mr3y.podcaster.ui.components.AddToQueueButton
import com.mr3y.podcaster.ui.components.DownloadButton
import com.mr3y.podcaster.ui.components.Error
import com.mr3y.podcaster.ui.components.LoadingIndicator
import com.mr3y.podcaster.ui.components.PlayPauseCompactButton
import com.mr3y.podcaster.ui.components.PullToRefresh
import com.mr3y.podcaster.ui.components.RemoveFromQueueButton
import com.mr3y.podcaster.ui.components.TopBar
import com.mr3y.podcaster.ui.components.rememberHtmlToAnnotatedString
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.RefreshResult
import com.mr3y.podcaster.ui.presenter.episodedetails.EpisodeDetailsUIEvent
import com.mr3y.podcaster.ui.presenter.episodedetails.EpisodeDetailsUIState
import com.mr3y.podcaster.ui.presenter.episodedetails.EpisodeDetailsViewModel
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.isAppThemeDark
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight
import com.mr3y.podcaster.ui.utils.rememberFormattedEpisodeDate

@Composable
fun EpisodeDetailsScreen(
    onNavigateUp: () -> Unit,
    appState: PodcasterAppState,
    contentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
    viewModel: EpisodeDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentlyPlayingEpisode by appState.currentlyPlayingEpisode.collectAsStateWithLifecycle()
    EpisodeDetailsScreen(
        state = state,
        onNavigateUp = onNavigateUp,
        isSelected = state.episode?.id == currentlyPlayingEpisode?.episode?.id,
        playingStatus = currentlyPlayingEpisode?.playingStatus,
        externalContentPadding = contentPadding,
        excludedWindowInsets = excludedWindowInsets,
        eventSink = { event ->
            when (event) {
                is EpisodeDetailsUIEvent.Refresh -> viewModel.refresh()
                is EpisodeDetailsUIEvent.RefreshResultConsumed -> viewModel.consumeRefreshResult()
                is EpisodeDetailsUIEvent.Retry -> viewModel.retry()
                is EpisodeDetailsUIEvent.PlayEpisode -> appState.play(event.episode)
                is EpisodeDetailsUIEvent.Pause -> appState.pause()
                is EpisodeDetailsUIEvent.DownloadEpisode -> appState.downloadEpisode(event.episode)
                is EpisodeDetailsUIEvent.ResumeDownloading -> appState.resumeDownloading(event.episodeId)
                is EpisodeDetailsUIEvent.PauseDownloading -> appState.pauseDownloading(event.episodeId)
                is EpisodeDetailsUIEvent.AddEpisodeToQueue -> appState.addToQueue(event.episode)
                is EpisodeDetailsUIEvent.RemoveEpisodeFromQueue -> appState.removeFromQueue(event.episodeId)
                is EpisodeDetailsUIEvent.ErrorPlayingStatusConsumed -> appState.consumeErrorPlayingStatus()
            }
        },
        modifier = modifier,
    )
}

@Composable
fun EpisodeDetailsScreen(
    state: EpisodeDetailsUIState,
    onNavigateUp: () -> Unit,
    isSelected: Boolean,
    playingStatus: PlayingStatus?,
    externalContentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    eventSink: (EpisodeDetailsUIEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val strings = LocalStrings.current
    val isDarkTheme = isAppThemeDark()
    val context = LocalContext.current
    LaunchedEffect(state.refreshResult, playingStatus) {
        when (state.refreshResult) {
            is RefreshResult.Error -> {
                snackBarHostState.showSnackbar(
                    message = strings.episode_details_refresh_result_error,
                )
                eventSink(EpisodeDetailsUIEvent.RefreshResultConsumed)
            }
            is RefreshResult.Mixed -> {
                snackBarHostState.showSnackbar(
                    message = strings.episode_details_refresh_result_mixed,
                )
                eventSink(EpisodeDetailsUIEvent.RefreshResultConsumed)
            }
            is RefreshResult.Ok, null -> {}
        }
        when (playingStatus) {
            PlayingStatus.Error -> {
                snackBarHostState.showSnackbar(
                    message = strings.generic_error_message,
                )
                eventSink(EpisodeDetailsUIEvent.ErrorPlayingStatusConsumed)
            }
            else -> {}
        }
    }
    LaunchedEffect(key1 = isDarkTheme) {
        context.setStatusBarAppearanceLight(isAppearanceLight = !isDarkTheme)
    }
    PullToRefresh(
        isRefreshingDone = !state.isRefreshing,
        onRefresh = { eventSink(EpisodeDetailsUIEvent.Refresh) },
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    isTopLevelScreen = false,
                    onNavIconClick = onNavigateUp,
                    title = {
                        Text(
                            text = state.episode?.podcastTitle ?: "",
                            color = MaterialTheme.colorScheme.inverseSurface,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState, Modifier.padding(externalContentPadding)) },
            contentWindowInsets = if (excludedWindowInsets != null) ScaffoldDefaults.contentWindowInsets.exclude(excludedWindowInsets) else ScaffoldDefaults.contentWindowInsets,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = modifier,
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                when {
                    state.isLoading -> {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),
                        )
                    }
                    state.episode == null -> {
                        Error(
                            onRetry = { eventSink(EpisodeDetailsUIEvent.Retry) },
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),
                        )
                    }
                    else -> {
                        val urlHandler = LocalUriHandler.current
                        EpisodeDetails(
                            episode = state.episode,
                            downloadMetadata = state.downloadMetadata,
                            onPlay = { eventSink(EpisodeDetailsUIEvent.PlayEpisode(it)) },
                            onPause = { eventSink(EpisodeDetailsUIEvent.Pause) },
                            queueEpisodes = state.queueEpisodesIds,
                            onAddEpisodeToQueue = { eventSink(EpisodeDetailsUIEvent.AddEpisodeToQueue(it)) },
                            onRemoveEpisodeFromQueue = { eventSink(EpisodeDetailsUIEvent.RemoveEpisodeFromQueue(it)) },
                            onDownloadingEpisode = { eventSink(EpisodeDetailsUIEvent.DownloadEpisode(it)) },
                            onResumeDownloadingEpisode = { eventSink(EpisodeDetailsUIEvent.ResumeDownloading(it)) },
                            onPauseDownloadingEpisode = { eventSink(EpisodeDetailsUIEvent.PauseDownloading(it)) },
                            isSelected = isSelected,
                            playingStatus = playingStatus,
                            externalContentPadding = externalContentPadding,
                            onUrlClick = urlHandler::openUri,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun EpisodeDetails(
    episode: Episode,
    downloadMetadata: EpisodeDownloadMetadata?,
    onPlay: (Episode) -> Unit,
    onPause: () -> Unit,
    queueEpisodes: List<Long>,
    onAddEpisodeToQueue: (Episode) -> Unit,
    onRemoveEpisodeFromQueue: (episodeId: Long) -> Unit,
    onDownloadingEpisode: (Episode) -> Unit,
    onResumeDownloadingEpisode: (episodeId: Long) -> Unit,
    onPauseDownloadingEpisode: (episodeId: Long) -> Unit,
    isSelected: Boolean,
    playingStatus: PlayingStatus?,
    externalContentPadding: PaddingValues,
    onUrlClick: (url: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .padding(externalContentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val context = LocalContext.current
            val imageSize = 128
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(episode.artworkUrl)
                    .size(imageSize)
                    .scale(Scale.FILL)
                    .allowHardware(false)
                    .memoryCacheKey("${episode.artworkUrl}.palette")
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(imageSize.dp),
            )
        }
        val formattedEpisodeDate = rememberFormattedEpisodeDate(episode)
        Text(
            text = formattedEpisodeDate,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = episode.title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            PlayPauseCompactButton(
                isSelected = isSelected,
                playingStatus = playingStatus,
                onPlay = { onPlay(episode) },
                onPause = onPause,
                containerColor = MaterialTheme.colorScheme.primaryTertiary,
                contentColor = MaterialTheme.colorScheme.onPrimaryTertiary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (episode.id !in queueEpisodes) {
                AddToQueueButton(
                    onClick = { onAddEpisodeToQueue(episode) },
                    contentColor = MaterialTheme.colorScheme.primaryTertiary,
                )
            } else {
                RemoveFromQueueButton(
                    onClick = { onRemoveEpisodeFromQueue(episode.id) },
                    contentColor = MaterialTheme.colorScheme.primaryTertiary,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            DownloadButton(
                downloadMetadata = downloadMetadata,
                onDownload = { onDownloadingEpisode(episode) },
                onResumingDownload = { onResumeDownloadingEpisode(episode.id) },
                onPausingDownload = { onPauseDownloadingEpisode(episode.id) },
                contentColor = MaterialTheme.colorScheme.primaryTertiary,
            )
        }
        val styledDescription = rememberHtmlToAnnotatedString(episode.description)
        ClickableText(
            text = styledDescription,
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            onClick = { position ->
                styledDescription
                    .getUrlAnnotations(position, position)
                    .firstOrNull()?.let { range -> onUrlClick(range.item.url) }
            },
        )
    }
}

@PodcasterPreview
@Composable
fun EpisodeDetailsScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        EpisodeDetailsScreen(
            state = EpisodeDetailsUIState(
                isLoading = false,
                episode = EpisodeWithDetails,
                queueEpisodesIds = emptyList(),
                isRefreshing = false,
                refreshResult = null,
                downloadMetadata = DownloadMetadata,
            ),
            onNavigateUp = {},
            isSelected = false,
            playingStatus = null,
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            eventSink = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@PodcasterPreview
@Composable
fun EpisodeDetailsErrorPreview() {
    PodcasterTheme(dynamicColor = false) {
        EpisodeDetailsScreen(
            state = EpisodeDetailsUIState(
                isLoading = false,
                episode = null,
                queueEpisodesIds = emptyList(),
                isRefreshing = false,
                refreshResult = null,
                downloadMetadata = null,
            ),
            onNavigateUp = {},
            isSelected = false,
            playingStatus = null,
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            eventSink = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
