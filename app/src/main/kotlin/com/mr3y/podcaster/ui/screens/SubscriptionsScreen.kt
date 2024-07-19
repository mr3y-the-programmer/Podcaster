package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeWithDownloadMetadata
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.core.sampledata.Episodes
import com.mr3y.podcaster.core.sampledata.EpisodesWithDownloadMetadata
import com.mr3y.podcaster.core.sampledata.Podcasts
import com.mr3y.podcaster.ui.components.AddToQueueButton
import com.mr3y.podcaster.ui.components.AnimatedAsyncImage
import com.mr3y.podcaster.ui.components.LoadingIndicator
import com.mr3y.podcaster.ui.components.LocalAnimatedVisibilityScope
import com.mr3y.podcaster.ui.components.LocalSharedTransitionScope
import com.mr3y.podcaster.ui.components.PlayPauseCompactButton
import com.mr3y.podcaster.ui.components.PullToRefresh
import com.mr3y.podcaster.ui.components.RemoveFromQueueButton
import com.mr3y.podcaster.ui.components.TopBar
import com.mr3y.podcaster.ui.components.rememberHtmlToAnnotatedString
import com.mr3y.podcaster.ui.components.rememberSharedContentState
import com.mr3y.podcaster.ui.components.sharedElement
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.RefreshResult
import com.mr3y.podcaster.ui.presenter.subscriptions.SubscriptionsUIEvent
import com.mr3y.podcaster.ui.presenter.subscriptions.SubscriptionsUIState
import com.mr3y.podcaster.ui.presenter.subscriptions.SubscriptionsViewModel
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.isAppThemeDark
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight
import com.mr3y.podcaster.ui.utils.dateSharedTransitionKey
import com.mr3y.podcaster.ui.utils.rememberFormattedEpisodeDate

@Composable
fun SubscriptionsScreen(
    onPodcastClick: (podcastId: Long) -> Unit,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
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
        currentlyPlayingEpisode = currentlyPlayingEpisode,
        externalContentPadding = contentPadding,
        excludedWindowInsets = excludedWindowInsets,
        eventSink = { event ->
            when (event) {
                is SubscriptionsUIEvent.Refresh -> viewModel.refresh()
                is SubscriptionsUIEvent.RefreshResultConsumed -> viewModel.consumeRefreshResult()
                is SubscriptionsUIEvent.PlayEpisode -> appState.play(event.episode)
                is SubscriptionsUIEvent.Pause -> appState.pause()
                is SubscriptionsUIEvent.AddEpisodeToQueue -> appState.addToQueue(event.episode)
                is SubscriptionsUIEvent.RemoveEpisodeFromQueue -> appState.removeFromQueue(event.episodeId)
                is SubscriptionsUIEvent.ErrorPlayingStatusConsumed -> appState.consumeErrorPlayingStatus()
            }
        },
        modifier = modifier,
    )
}

@Composable
fun SubscriptionsScreen(
    state: SubscriptionsUIState,
    onPodcastClick: (podcastId: Long) -> Unit,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    currentlyPlayingEpisode: CurrentlyPlayingEpisode?,
    externalContentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    eventSink: (SubscriptionsUIEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val strings = LocalStrings.current
    val context = LocalContext.current
    val isDarkTheme = isAppThemeDark()
    val playingStatus = currentlyPlayingEpisode?.playingStatus
    LaunchedEffect(state.refreshResult, playingStatus) {
        when (state.refreshResult) {
            is RefreshResult.Error -> {
                snackBarHostState.showSnackbar(
                    message = strings.subscriptions_refresh_result_error,
                )
                eventSink(SubscriptionsUIEvent.RefreshResultConsumed)
            }
            is RefreshResult.Mixed -> {
                snackBarHostState.showSnackbar(
                    message = strings.subscriptions_refresh_result_mixed,
                )
                eventSink(SubscriptionsUIEvent.RefreshResultConsumed)
            }
            is RefreshResult.Ok, null -> {}
        }
        when (playingStatus) {
            PlayingStatus.Error -> {
                snackBarHostState.showSnackbar(
                    message = strings.generic_error_message,
                )
                eventSink(SubscriptionsUIEvent.ErrorPlayingStatusConsumed)
            }
            else -> {}
        }
    }
    LaunchedEffect(key1 = isDarkTheme) {
        context.setStatusBarAppearanceLight(isAppearanceLight = isDarkTheme)
    }
    PullToRefresh(
        isRefreshing = state.isRefreshing,
        onRefresh = { eventSink(SubscriptionsUIEvent.Refresh) },
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onUpButtonClick = null,
                    title = {
                        if (state.subscriptions.isNotEmpty()) {
                            Text(
                                text = strings.subscriptions_label,
                                color = MaterialTheme.colorScheme.onPrimaryTertiary,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryTertiary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryTertiary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryTertiary,
                    ),
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
                        onPlayEpisode = { eventSink(SubscriptionsUIEvent.PlayEpisode(it)) },
                        onPause = { eventSink(SubscriptionsUIEvent.Pause) },
                        queueEpisodes = state.queueEpisodesIds,
                        onAddEpisodeToQueue = { eventSink(SubscriptionsUIEvent.AddEpisodeToQueue(it)) },
                        onRemoveEpisodeFromQueue = { eventSink(SubscriptionsUIEvent.RemoveEpisodeFromQueue(it)) },
                        currentlyPlayingEpisode = currentlyPlayingEpisode,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.SubscriptionsHeader(
    isLoading: Boolean,
    podcasts: List<Podcast>,
    onPodcastClick: (podcastId: Long) -> Unit,
) {
    val strings = LocalStrings.current
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
                    AnimatedAsyncImage(
                        artworkUrl = podcast.artworkUrl,
                        sharedTransitionKey = podcast.id.toString(),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(120.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = { onPodcastClick(podcast.id) }),
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
    queueEpisodes: List<Long>,
    onAddEpisodeToQueue: (Episode) -> Unit,
    onRemoveEpisodeFromQueue: (episodeId: Long) -> Unit,
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
                    itemsIndexed(episodes, key = { _, (episode, _) -> episode.id }) { index, (episode, _) ->
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
                                AnimatedAsyncImage(
                                    artworkUrl = episode.artworkUrl,
                                    sharedTransitionKey = episode.id.toString(),
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp)),
                                )
                                val formattedEpisodeDate = rememberFormattedEpisodeDate(episode)
                                Text(
                                    text = formattedEpisodeDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.inverseSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.sharedElement(
                                        LocalSharedTransitionScope.current,
                                        LocalAnimatedVisibilityScope.current,
                                        rememberSharedContentState(key = episode.dateSharedTransitionKey),
                                    ),
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
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                PlayPauseCompactButton(
                                    isSelected = currentlyPlayingEpisode != null && currentlyPlayingEpisode.episode.id == episode.id,
                                    playingStatus = currentlyPlayingEpisode?.playingStatus,
                                    onPlay = { onPlayEpisode(episode) },
                                    onPause = onPause,
                                )
                                if (episode.id !in queueEpisodes) {
                                    AddToQueueButton(
                                        onClick = { onAddEpisodeToQueue(episode) },
                                    )
                                } else {
                                    RemoveFromQueueButton(
                                        onClick = { onRemoveEpisodeFromQueue(episode.id) },
                                    )
                                }
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
                    queueEpisodesIds = Episodes.take(2).map { it.id },
                ),
            )
        }
        SubscriptionsScreen(
            state = state,
            onPodcastClick = {},
            onEpisodeClick = { _, _ -> },
            currentlyPlayingEpisode = null,
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            eventSink = {},
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
                    queueEpisodesIds = Episodes.take(2).map { it.id },
                ),
            )
        }
        SubscriptionsScreen(
            state = state,
            onPodcastClick = {},
            onEpisodeClick = { _, _ -> },
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            currentlyPlayingEpisode = null,
            eventSink = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
