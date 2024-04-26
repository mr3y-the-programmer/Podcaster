package com.mr3y.podcaster.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.kmpalette.rememberDominantColorState
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.R
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.core.sampledata.Episodes
import com.mr3y.podcaster.core.sampledata.PodcastWithDetails
import com.mr3y.podcaster.ui.components.AddToQueueButton
import com.mr3y.podcaster.ui.components.Error
import com.mr3y.podcaster.ui.components.LoadingIndicator
import com.mr3y.podcaster.ui.components.PlayPauseCompactButton
import com.mr3y.podcaster.ui.components.PullToRefresh
import com.mr3y.podcaster.ui.components.RemoveFromQueueButton
import com.mr3y.podcaster.ui.components.TopBar
import com.mr3y.podcaster.ui.components.rememberHtmlToAnnotatedString
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.RefreshResult
import com.mr3y.podcaster.ui.presenter.podcastdetails.PodcastDetailsUIEvent
import com.mr3y.podcaster.ui.presenter.podcastdetails.PodcastDetailsUIState
import com.mr3y.podcaster.ui.presenter.podcastdetails.PodcastDetailsViewModel
import com.mr3y.podcaster.ui.presenter.podcastdetails.SubscriptionState
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
fun PodcastDetailsScreen(
    onNavigateUp: () -> Unit,
    onEpisodeClick: (episodeId: Long, podcastArtworkUrl: String) -> Unit,
    appState: PodcasterAppState,
    contentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
    viewModel: PodcastDetailsViewModel = hiltViewModel(),
) {
    val podcastDetailsState by viewModel.state.collectAsStateWithLifecycle()
    val currentlyPlayingEpisode by appState.currentlyPlayingEpisode.collectAsStateWithLifecycle()
    PodcastDetailsScreen(
        state = podcastDetailsState,
        onNavigateUp = onNavigateUp,
        currentlyPlayingEpisode = currentlyPlayingEpisode,
        externalContentPadding = contentPadding,
        excludedWindowInsets = excludedWindowInsets,
        onEpisodeClick = onEpisodeClick,
        eventSink = { event ->
            when (event) {
                is PodcastDetailsUIEvent.Subscribe -> viewModel.subscribe()
                is PodcastDetailsUIEvent.UnSubscribe -> viewModel.unsubscribe()
                is PodcastDetailsUIEvent.Refresh -> viewModel.refresh()
                is PodcastDetailsUIEvent.RefreshResultConsumed -> viewModel.consumeRefreshResult()
                is PodcastDetailsUIEvent.Retry -> viewModel.retry()
                is PodcastDetailsUIEvent.PlayEpisode -> appState.play(event.episode)
                is PodcastDetailsUIEvent.Pause -> appState.pause()
                is PodcastDetailsUIEvent.AddEpisodeToQueue -> appState.addToQueue(event.episode)
                is PodcastDetailsUIEvent.RemoveEpisodeFromQueue -> appState.removeFromQueue(event.episodeId)
                is PodcastDetailsUIEvent.ErrorPlayingStatusConsumed -> appState.consumeErrorPlayingStatus()
            }
        },
        modifier = modifier,
    )
}

@Composable
fun PodcastDetailsScreen(
    state: PodcastDetailsUIState,
    onNavigateUp: () -> Unit,
    currentlyPlayingEpisode: CurrentlyPlayingEpisode?,
    externalContentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    onEpisodeClick: (episodeId: Long, podcastArtworkUrl: String) -> Unit,
    eventSink: (PodcastDetailsUIEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val strings = LocalStrings.current
    val playingStatus = currentlyPlayingEpisode?.playingStatus
    val isDarkTheme = isAppThemeDark()
    val context = LocalContext.current
    LaunchedEffect(state.refreshResult, playingStatus) {
        when (state.refreshResult) {
            is RefreshResult.Error -> {
                snackBarHostState.showSnackbar(
                    message = strings.podcast_details_refresh_result_error,
                )
                eventSink(PodcastDetailsUIEvent.RefreshResultConsumed)
            }
            is RefreshResult.Mixed -> {
                snackBarHostState.showSnackbar(
                    message = strings.podcast_details_refresh_result_mixed,
                )
                eventSink(PodcastDetailsUIEvent.RefreshResultConsumed)
            }
            is RefreshResult.Ok, null -> {}
        }
        when (playingStatus) {
            PlayingStatus.Error -> {
                snackBarHostState.showSnackbar(
                    message = strings.generic_error_message,
                )
                eventSink(PodcastDetailsUIEvent.ErrorPlayingStatusConsumed)
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
        if (state.isPodcastLoading || state.podcast == null) {
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
        onRefresh = { eventSink(PodcastDetailsUIEvent.Refresh) },
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    isTopLevelScreen = false,
                    onNavIconClick = onNavigateUp,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (state.isPodcastLoading || state.podcast == null) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            dominantColorState.color
                        },
                        navigationIconContentColor = if (state.isPodcastLoading || state.podcast == null) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            dominantColorState.onColor
                        },
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            snackbarHost = { SnackbarHost(snackBarHostState, Modifier.padding(externalContentPadding)) },
            contentWindowInsets = if (excludedWindowInsets != null) ScaffoldDefaults.contentWindowInsets.exclude(excludedWindowInsets) else ScaffoldDefaults.contentWindowInsets,
            modifier = modifier,
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
            ) {
                when {
                    state.isPodcastLoading -> {
                        LoadingIndicator(modifier = Modifier.fillMaxSize())
                    }
                    state.podcast == null -> {
                        Error(onRetry = { eventSink(PodcastDetailsUIEvent.Retry) }, modifier = Modifier.fillMaxSize())
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(bottom = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = externalContentPadding,
                        ) {
                            item {
                                Header(
                                    artworkUrl = state.podcast.artworkUrl,
                                    onState = { state ->
                                        when (state) {
                                            is AsyncImagePainter.State.Success -> bitmap = state.result.drawable.toBitmap().asImageBitmap()
                                            else -> {}
                                        }
                                    },
                                    dominantColor = dominantColorState.color,
                                    subscriptionState = state.subscriptionState,
                                    isSubscriptionInEditMode = state.isSubscriptionStateInEditMode,
                                    onSubscribe = { eventSink(PodcastDetailsUIEvent.Subscribe) },
                                    onUnsubscribe = { eventSink(PodcastDetailsUIEvent.UnSubscribe) },
                                )
                            }
                            item {
                                val urlHandler = LocalUriHandler.current
                                Info(
                                    podcast = state.podcast,
                                    onUrlClick = { url -> urlHandler.openUri(url) },
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }

                            when {
                                state.isEpisodesLoading -> {
                                    item {
                                        LoadingIndicator(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .fillParentMaxWidth()
                                                .height(144.dp),
                                        )
                                    }
                                }
                                state.episodes == null -> {
                                    item {
                                        Error(
                                            onRetry = { eventSink(PodcastDetailsUIEvent.Retry) },
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .fillParentMaxWidth()
                                                .height(144.dp),
                                        )
                                    }
                                }
                                else -> {
                                    itemsIndexed(
                                        state.episodes,
                                        key = { _, episode -> episode.id },
                                    ) { index, episode ->
                                        Episode(
                                            episode = episode,
                                            onEpisodeClick = { episodeId -> onEpisodeClick(episodeId, state.podcast.artworkUrl) },
                                            currentlyPlayingEpisode = currentlyPlayingEpisode,
                                            onPlay = { eventSink(PodcastDetailsUIEvent.PlayEpisode(it)) },
                                            onPause = { eventSink(PodcastDetailsUIEvent.Pause) },
                                            queueEpisodes = state.queueEpisodesIds,
                                            onAddEpisodeToQueue = { eventSink(PodcastDetailsUIEvent.AddEpisodeToQueue(it)) },
                                            onRemoveEpisodeFromQueue = { eventSink(PodcastDetailsUIEvent.RemoveEpisodeFromQueue(it)) },
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                        )
                                        if (index != state.episodes.lastIndex) {
                                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    artworkUrl: String,
    onState: ((AsyncImagePainter.State) -> Unit)?,
    dominantColor: Color,
    subscriptionState: SubscriptionState,
    isSubscriptionInEditMode: Boolean,
    onSubscribe: () -> Unit,
    onUnsubscribe: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        val imageSize = 128
        val context = LocalContext.current
        Box(
            Modifier
                .height((imageSize * 3f / 4f).dp)
                .fillMaxWidth()
                .background(dominantColor),
        )

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(artworkUrl)
                .size(imageSize)
                .scale(Scale.FILL)
                .allowHardware(false)
                .memoryCacheKey("$artworkUrl.palette")
                .build(),
            onState = onState,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = (imageSize * 1f / 4f).dp)
                .size(imageSize.dp)
                .border(2.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp)),
        )

        val strings = LocalStrings.current
        AnimatedContent(
            targetState = subscriptionState,
            transitionSpec = {
                (
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90))
                    )
                    .togetherWith(fadeOut(animationSpec = tween(90)))
                    .using(SizeTransform(clip = false, sizeAnimationSpec = { _, _ -> tween(600) }))
            },
            label = "Toggle Subscription state",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .padding(top = 8.dp),
        ) { targetState ->
            when (targetState) {
                SubscriptionState.Subscribed -> {
                    OutlinedButton(
                        onClick = onUnsubscribe,
                        enabled = !isSubscriptionInEditMode,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primaryTertiary,
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryTertiary),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.unsubscribe_label,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
                SubscriptionState.NotSubscribed -> {
                    ElevatedButton(
                        onClick = onSubscribe,
                        enabled = !isSubscriptionInEditMode,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryTertiary,
                            contentColor = MaterialTheme.colorScheme.onPrimaryTertiary,
                        ),
                    ) {
                        Text(
                            text = strings.subscribe_label,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Info(
    podcast: Podcast,
    onUrlClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = podcast.title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = podcast.author,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .widthIn(max = 232.dp)
                    .basicMarquee(initialDelayMillis = 300),
            )
            IconButton(
                onClick = { onUrlClick(podcast.website) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.inverseSurface,
                ),
                modifier = Modifier.requiredSize(48.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.world_wide_web),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.inverseSurface,
                )
            }
        }
        if (podcast.genres.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                podcast.genres.forEach { genre ->
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(text = genre.label)
                        },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        val strings = LocalStrings.current
        Text(
            text = strings.about_label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = podcast.description,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = strings.episodes_label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = podcast.episodeCount.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun Episode(
    episode: Episode,
    onEpisodeClick: (episodeId: Long) -> Unit,
    currentlyPlayingEpisode: CurrentlyPlayingEpisode?,
    queueEpisodes: List<Long>,
    onAddEpisodeToQueue: (Episode) -> Unit,
    onRemoveEpisodeFromQueue: (episodeId: Long) -> Unit,
    onPlay: (Episode) -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onEpisodeClick(episode.id) }),
    ) {
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PlayPauseCompactButton(
                isSelected = currentlyPlayingEpisode != null && currentlyPlayingEpisode.episode.id == episode.id,
                playingStatus = currentlyPlayingEpisode?.playingStatus,
                onPlay = { onPlay(episode) },
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
}

@PodcasterPreview
@Composable
fun PodcastDetailsScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        PodcastDetailsScreen(
            state = PodcastDetailsUIState(
                isPodcastLoading = false,
                isEpisodesLoading = false,
                podcast = PodcastWithDetails,
                subscriptionState = SubscriptionState.NotSubscribed,
                isSubscriptionStateInEditMode = false,
                episodes = Episodes.take(4),
                isRefreshing = false,
                refreshResult = null,
                queueEpisodesIds = Episodes.take(1).map { it.id },
            ),
            onNavigateUp = {},
            currentlyPlayingEpisode = null,
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            onEpisodeClick = { _, _ -> },
            eventSink = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@PodcasterPreview
@Composable
fun PodcastDetailsScreenErrorPreview() {
    PodcasterTheme(dynamicColor = false) {
        PodcastDetailsScreen(
            state = PodcastDetailsUIState(
                isPodcastLoading = false,
                isEpisodesLoading = false,
                podcast = null,
                subscriptionState = SubscriptionState.NotSubscribed,
                isSubscriptionStateInEditMode = false,
                episodes = null,
                isRefreshing = false,
                refreshResult = null,
                queueEpisodesIds = Episodes.take(1).map { it.id },
            ),
            onNavigateUp = {},
            currentlyPlayingEpisode = null,
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            onEpisodeClick = { _, _ -> },
            eventSink = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@PodcasterPreview
@Composable
fun PodcastDetailsScreenEpisodesErrorPreview() {
    PodcasterTheme(dynamicColor = false) {
        PodcastDetailsScreen(
            state = PodcastDetailsUIState(
                isPodcastLoading = false,
                isEpisodesLoading = false,
                podcast = PodcastWithDetails,
                subscriptionState = SubscriptionState.NotSubscribed,
                isSubscriptionStateInEditMode = false,
                episodes = null,
                isRefreshing = false,
                refreshResult = null,
                queueEpisodesIds = Episodes.take(1).map { it.id },
            ),
            onNavigateUp = {},
            currentlyPlayingEpisode = null,
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            onEpisodeClick = { _, _ -> },
            eventSink = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
