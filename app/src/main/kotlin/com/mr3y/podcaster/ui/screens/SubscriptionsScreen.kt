package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeWithDownloadMetadata
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.core.model.dateTimePublished
import com.mr3y.podcaster.ui.components.DownloadButton
import com.mr3y.podcaster.ui.components.LoadingIndicator
import com.mr3y.podcaster.ui.components.PlayPauseCompactButton
import com.mr3y.podcaster.ui.components.PullToRefresh
import com.mr3y.podcaster.ui.components.rememberHtmlToAnnotatedString
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.RefreshResult
import com.mr3y.podcaster.ui.presenter.subscriptions.SubscriptionsUIState
import com.mr3y.podcaster.ui.presenter.subscriptions.SubscriptionsViewModel
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.EpisodesWithDownloadMetadata
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.preview.Podcasts
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SubscriptionsScreen(
    onPodcastClick: (podcastId: Long) -> Unit,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    onSettingsClick: () -> Unit,
    onNavDrawerClick: () -> Unit,
    appState: PodcasterAppState,
    contentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
    viewModel: SubscriptionsViewModel = hiltViewModel(),
) {
    val subscriptionsState by viewModel.state.collectAsStateWithLifecycle()
    val currentlyPlayingEpisode by appState.currentlyPlayingEpisode.collectAsStateWithLifecycle()
    SubscriptionsScreen(
        state = subscriptionsState,
        onPodcastClick = onPodcastClick,
        onEpisodeClick = onEpisodeClick,
        onSettingsClick = onSettingsClick,
        onNavDrawerClick = onNavDrawerClick,
        onRefresh = viewModel::refresh,
        onRefreshResultConsumed = viewModel::consumeRefreshResult,
        onPlayEpisode = appState::play,
        onPause = appState::pause,
        onDownloadingEpisode = appState::downloadEpisode,
        onResumeDownloadingEpisode = appState::resumeDownloading,
        onPauseDownloadingEpisode = appState::pauseDownloading,
        currentlyPlayingEpisode = currentlyPlayingEpisode,
        onConsumeErrorPlayingStatus = appState::consumeErrorPlayingStatus,
        externalContentPadding = contentPadding,
        excludedWindowInsets = excludedWindowInsets,
        modifier = modifier,
    )
}

@Composable
fun SubscriptionsScreen(
    state: SubscriptionsUIState,
    onPodcastClick: (podcastId: Long) -> Unit,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    onSettingsClick: () -> Unit,
    onNavDrawerClick: () -> Unit,
    onPlayEpisode: (Episode) -> Unit,
    onPause: () -> Unit,
    onDownloadingEpisode: (Episode) -> Unit,
    onResumeDownloadingEpisode: (episodeId: Long) -> Unit,
    onPauseDownloadingEpisode: (episodeId: Long) -> Unit,
    currentlyPlayingEpisode: CurrentlyPlayingEpisode?,
    onConsumeErrorPlayingStatus: () -> Unit,
    externalContentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    onRefresh: () -> Unit,
    onRefreshResultConsumed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val strings = LocalStrings.current
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val playingStatus = currentlyPlayingEpisode?.playingStatus
    LaunchedEffect(state.refreshResult, playingStatus) {
        when (state.refreshResult) {
            is RefreshResult.Error -> {
                snackBarHostState.showSnackbar(
                    message = strings.subscriptions_refresh_result_error,
                )
                onRefreshResultConsumed()
            }
            is RefreshResult.Mixed -> {
                snackBarHostState.showSnackbar(
                    message = strings.subscriptions_refresh_result_mixed,
                )
                onRefreshResultConsumed()
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
    LaunchedEffect(key1 = isDarkTheme) {
        context.setStatusBarAppearanceLight(isAppearanceLight = isDarkTheme)
    }
    PullToRefresh(
        isRefreshingDone = !state.isRefreshing,
        onRefresh = onRefresh,
    ) {
        Scaffold(
            topBar = {
                SubscriptionsTopAppBar(
                    onSettingsClick = onSettingsClick,
                    onNavDrawerClick = onNavDrawerClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier.padding(externalContentPadding),
                )
            },
            contentWindowInsets = if (excludedWindowInsets != null) ScaffoldDefaults.contentWindowInsets.exclude(excludedWindowInsets) else ScaffoldDefaults.contentWindowInsets,
            modifier = modifier,
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryTertiary),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SubscriptionsHeader(
                        isLoading = state.isSubscriptionsLoading,
                        podcasts = state.subscriptions,
                        onPodcastClick = onPodcastClick,
                    )
                    EpisodesList(
                        isLoading = state.isEpisodesLoading,
                        episodes = state.episodes,
                        contentPadding = externalContentPadding,
                        onEpisodeClick = onEpisodeClick,
                        onPlayEpisode = onPlayEpisode,
                        onPause = onPause,
                        onDownloadingEpisode = onDownloadingEpisode,
                        onResumeDownloadingEpisode = onResumeDownloadingEpisode,
                        onPauseDownloadingEpisode = onPauseDownloadingEpisode,
                        currentlyPlayingEpisode = currentlyPlayingEpisode,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscriptionsTopAppBar(
    onSettingsClick: () -> Unit,
    onNavDrawerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onNavDrawerClick,
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = strings.icon_menu_content_description,
                )
            }
        },
        title = { },
        actions = {
            IconButton(
                onClick = onSettingsClick,
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = strings.icon_settings_content_description,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryTertiary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryTertiary,
        ),
        modifier = modifier,
    )
}

@Composable
private fun ColumnScope.SubscriptionsHeader(
    isLoading: Boolean,
    podcasts: List<Podcast>,
    onPodcastClick: (podcastId: Long) -> Unit,
) {
    val strings = LocalStrings.current
    Text(
        text = strings.subscriptions_label,
        color = MaterialTheme.colorScheme.onPrimaryTertiary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
    )
    if (isLoading) {
        Spacer(modifier = Modifier.height(40.dp))
        LoadingIndicator(
            color = MaterialTheme.colorScheme.onPrimaryTertiary,
            modifier = Modifier.fillMaxWidth(),
        )
    } else {
        if (podcasts.isNotEmpty()) {
            val edgeWidth = 32.dp
            val color = MaterialTheme.colorScheme.primaryTertiary
            fun ContentDrawScope.drawFadedEdge(leftEdge: Boolean) {
                val edgeWidthPx = edgeWidth.toPx()
                drawRect(
                    topLeft = Offset(if (leftEdge) 0f else size.width - edgeWidthPx, 0f),
                    size = Size(edgeWidthPx, size.height),
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, color),
                        startX = if (leftEdge) 0f else size.width,
                        endX = if (leftEdge) edgeWidthPx else size.width - edgeWidthPx,
                    ),
                    blendMode = BlendMode.DstIn,
                )
            }
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp + edgeWidth, end = 8.dp + edgeWidth),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawFadedEdge(leftEdge = true)
                        drawFadedEdge(leftEdge = false)
                    }
                    .verticalScroll(rememberScrollState()),
            ) {
                items(podcasts, key = { it.id }) { podcast ->
                    AsyncImage(
                        model = podcast.artworkUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = { onPodcastClick(podcast.id) }),
                        contentScale = ContentScale.FillBounds,
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = strings.subscriptions_empty_list,
                color = MaterialTheme.colorScheme.onPrimaryTertiary,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(120.dp),
            )
        }
    }
}

@Composable
private fun ColumnScope.EpisodesList(
    isLoading: Boolean,
    episodes: List<EpisodeWithDownloadMetadata>,
    contentPadding: PaddingValues,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    onPlayEpisode: (Episode) -> Unit,
    onPause: () -> Unit,
    onDownloadingEpisode: (Episode) -> Unit,
    onResumeDownloadingEpisode: (episodeId: Long) -> Unit,
    onPauseDownloadingEpisode: (episodeId: Long) -> Unit,
    currentlyPlayingEpisode: CurrentlyPlayingEpisode?,
) {
    Card(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
    ) {
        if (isLoading) {
            Spacer(modifier = Modifier.height(80.dp))
            LoadingIndicator(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            if (episodes.isNotEmpty()) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    itemsIndexed(episodes, key = { _, (episode, _) -> episode.id }) { index, (episode, downloadMetadata) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    onEpisodeClick(
                                        episode.id,
                                        episode.artworkUrl,
                                    )
                                })
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
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
                                val formattedEpisodeDate = remember(episode.datePublishedTimestamp) { format(episode.dateTimePublished) }
                                Text(
                                    text = formattedEpisodeDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.inverseSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
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
                                PlayPauseCompactButton(
                                    isSelected = currentlyPlayingEpisode != null && currentlyPlayingEpisode.episode.id == episode.id,
                                    playingStatus = currentlyPlayingEpisode?.playingStatus,
                                    onPlay = { onPlayEpisode(episode) },
                                    onPause = onPause,
                                )
                                DownloadButton(
                                    downloadMetadata = downloadMetadata,
                                    onDownload = { onDownloadingEpisode(episode) },
                                    onResumingDownload = { onResumeDownloadingEpisode(episode.id) },
                                    onPausingDownload = { onPauseDownloadingEpisode(episode.id) },
                                )
                            }
                        }
                        if (index != episodes.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            } else {
                val strings = LocalStrings.current
                Spacer(modifier = Modifier.height(80.dp))
                Text(
                    text = strings.subscriptions_episodes_empty_list,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

private fun format(dateTime: ZonedDateTime): String {
    val pattern = if (ZonedDateTime.now(ZoneId.systemDefault()).year != dateTime.year) "MMM d, yyyy" else "MMM d"
    return DateTimeFormatter.ofPattern(pattern).format(dateTime.toLocalDate())
}

@PodcasterPreview
@Composable
fun SubscriptionsScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        val state by remember {
            mutableStateOf(
                SubscriptionsUIState(
                    isSubscriptionsLoading = false,
                    isEpisodesLoading = false,
                    isRefreshing = false,
                    refreshResult = null,
                    subscriptions = Podcasts,
                    episodes = EpisodesWithDownloadMetadata,
                ),
            )
        }
        SubscriptionsScreen(
            state = state,
            onPodcastClick = {},
            onEpisodeClick = { _, _ -> },
            onSettingsClick = {},
            onNavDrawerClick = {},
            onRefresh = {},
            onRefreshResultConsumed = {},
            onPlayEpisode = {},
            onPause = {},
            onDownloadingEpisode = {},
            onResumeDownloadingEpisode = {},
            onPauseDownloadingEpisode = {},
            currentlyPlayingEpisode = null,
            onConsumeErrorPlayingStatus = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@PodcasterPreview
@Composable
fun EmptySubscriptionsScreenPreview() {
    PodcasterTheme(dynamicColor = false) {
        val state by remember {
            mutableStateOf(
                SubscriptionsUIState(
                    isSubscriptionsLoading = false,
                    isEpisodesLoading = false,
                    isRefreshing = false,
                    refreshResult = null,
                    subscriptions = emptyList(),
                    episodes = emptyList(),
                ),
            )
        }
        SubscriptionsScreen(
            state = state,
            onPodcastClick = {},
            onEpisodeClick = { _, _ -> },
            onSettingsClick = {},
            onNavDrawerClick = {},
            onRefresh = {},
            onRefreshResultConsumed = {},
            onPlayEpisode = {},
            onPause = {},
            onDownloadingEpisode = {},
            onResumeDownloadingEpisode = {},
            onPauseDownloadingEpisode = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            currentlyPlayingEpisode = null,
            onConsumeErrorPlayingStatus = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
