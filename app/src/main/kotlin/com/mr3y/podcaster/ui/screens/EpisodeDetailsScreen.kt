package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.kmpalette.rememberDominantColorState
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
import com.mr3y.podcaster.ui.presenter.episodedetails.EpisodeDetailsUIState
import com.mr3y.podcaster.ui.presenter.episodedetails.EpisodeDetailsViewModel
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.MinContrastRatio
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.contrastAgainst
import com.mr3y.podcaster.ui.theme.isAppThemeDark
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.onPrimaryTertiaryContainer
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiaryContainer
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight

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
        onRetry = viewModel::retry,
        onRefresh = viewModel::refresh,
        onPlayEpisode = appState::play,
        onPause = appState::pause,
        onAddEpisodeToQueue = appState::addToQueue,
        onRemoveEpisodeFromQueue = appState::removeFromQueue,
        onDownloadingEpisode = appState::downloadEpisode,
        onResumeDownloadingEpisode = appState::resumeDownloading,
        onPauseDownloadingEpisode = appState::pauseDownloading,
        isSelected = state.episode?.id == currentlyPlayingEpisode?.episode?.id,
        playingStatus = currentlyPlayingEpisode?.playingStatus,
        onConsumeErrorPlayingStatus = appState::consumeErrorPlayingStatus,
        externalContentPadding = contentPadding,
        excludedWindowInsets = excludedWindowInsets,
        onConsumeResult = viewModel::consumeRefreshResult,
        modifier = modifier,
    )
}

@Composable
fun EpisodeDetailsScreen(
    state: EpisodeDetailsUIState,
    onNavigateUp: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onPlayEpisode: (Episode) -> Unit,
    onPause: () -> Unit,
    onAddEpisodeToQueue: (Episode) -> Unit,
    onRemoveEpisodeFromQueue: (episodeId: Long) -> Unit,
    onDownloadingEpisode: (Episode) -> Unit,
    onResumeDownloadingEpisode: (episodeId: Long) -> Unit,
    onPauseDownloadingEpisode: (episodeId: Long) -> Unit,
    isSelected: Boolean,
    playingStatus: PlayingStatus?,
    onConsumeErrorPlayingStatus: () -> Unit,
    externalContentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    onConsumeResult: () -> Unit,
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
                onConsumeResult()
            }
            is RefreshResult.Mixed -> {
                snackBarHostState.showSnackbar(
                    message = strings.episode_details_refresh_result_mixed,
                )
                onConsumeResult()
            }
            is RefreshResult.Ok, null -> {}
        }
        when (playingStatus) {
            PlayingStatus.Error -> {
                snackBarHostState.showSnackbar(
                    message = strings.generic_error_message,
                )
                onConsumeErrorPlayingStatus()
            }
            else -> {}
        }
    }
    val dominantColorState = rememberDominantColorState(
        defaultColor = MaterialTheme.colorScheme.primaryTertiaryContainer,
        defaultOnColor = MaterialTheme.colorScheme.onPrimaryTertiaryContainer,
        cacheSize = 1,
        builder = {
            clearFilters()
                .maximumColorCount(8)
        },
    )
    var bitmap: ImageBitmap? by remember { mutableStateOf(null) }
    LaunchedEffect(bitmap) {
        val temp = bitmap
        if (temp != null) {
            dominantColorState.updateFrom(temp)
        }
    }
    val surfaceColor = MaterialTheme.colorScheme.surface
    LaunchedEffect(key1 = isDarkTheme, key2 = dominantColorState.onColor) {
        if (state.isLoading || state.episode == null) {
            context.setStatusBarAppearanceLight(isAppearanceLight = !isDarkTheme)
        } else {
            val contrastRatio = dominantColorState.onColor.contrastAgainst(surfaceColor)
            if (contrastRatio >= MinContrastRatio) {
                context.setStatusBarAppearanceLight(isAppearanceLight = !isDarkTheme)
            } else {
                context.setStatusBarAppearanceLight(isAppearanceLight = isDarkTheme)
            }
        }
    }
    PullToRefresh(
        isRefreshingDone = !state.isRefreshing,
        onRefresh = onRefresh,
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    isTopLevelScreen = false,
                    onNavIconClick = onNavigateUp,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (state.isLoading || state.episode == null) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            dominantColorState.color
                        },
                        navigationIconContentColor = if (state.isLoading || state.episode == null) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            dominantColorState.onColor
                        },
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
                            onRetry = onRetry,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),
                        )
                    }
                    else -> {
                        val urlHandler = LocalUriHandler.current
                        Header(
                            episode = state.episode,
                            downloadMetadata = state.downloadMetadata,
                            onPlay = onPlayEpisode,
                            onPause = onPause,
                            queueEpisodes = state.queueEpisodesIds,
                            onAddEpisodeToQueue = onAddEpisodeToQueue,
                            onRemoveEpisodeFromQueue = onRemoveEpisodeFromQueue,
                            onDownloadingEpisode = onDownloadingEpisode,
                            onResumeDownloadingEpisode = onResumeDownloadingEpisode,
                            onPauseDownloadingEpisode = onPauseDownloadingEpisode,
                            isSelected = isSelected,
                            playingStatus = playingStatus,
                            dominantColor = dominantColorState.color,
                            onState = { state ->
                                when (state) {
                                    is AsyncImagePainter.State.Success -> bitmap = state.result.drawable.toBitmap().asImageBitmap()
                                    else -> {}
                                }
                            },
                        )
                        Details(
                            episode = state.episode,
                            externalContentPadding = externalContentPadding,
                            onUrlClick = urlHandler::openUri,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.Header(
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
    dominantColor: Color,
    onState: ((AsyncImagePainter.State) -> Unit)?,
) {
    val context = LocalContext.current
    Box(
        Modifier
            .height(96.dp)
            .background(dominantColor)
            .fillMaxWidth(),
    )

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(episode.artworkUrl)
            .size(128)
            .scale(Scale.FILL)
            .allowHardware(false)
            .memoryCacheKey("${episode.artworkUrl}.palette")
            .build(),
        onState = onState,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp)
            .size(128.dp)
            .border(2.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
            .zIndex(3f),
    )

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = (32 + 64).dp)
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 8.dp)
            .zIndex(2f),
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
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun BoxScope.Details(
    episode: Episode,
    externalContentPadding: PaddingValues,
    onUrlClick: (url: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(top = 96.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .padding(top = 64.dp)
            .padding(externalContentPadding)
            .zIndex(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = episode.title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = episode.datePublishedFormatted,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
        )
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
            onRetry = {},
            onRefresh = {},
            onPlayEpisode = {},
            onPause = {},
            onAddEpisodeToQueue = {},
            onRemoveEpisodeFromQueue = {},
            onDownloadingEpisode = {},
            onResumeDownloadingEpisode = {},
            onPauseDownloadingEpisode = {},
            isSelected = false,
            playingStatus = null,
            onConsumeErrorPlayingStatus = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            onConsumeResult = {},
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
            onRetry = {},
            onRefresh = {},
            onPlayEpisode = {},
            onPause = {},
            onAddEpisodeToQueue = {},
            onRemoveEpisodeFromQueue = {},
            onDownloadingEpisode = {},
            onResumeDownloadingEpisode = {},
            onPauseDownloadingEpisode = {},
            isSelected = false,
            playingStatus = null,
            onConsumeErrorPlayingStatus = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            onConsumeResult = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
