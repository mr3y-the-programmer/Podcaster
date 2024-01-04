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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TopAppBar
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
import com.mr3y.podcaster.R
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.ui.components.DownloadButton
import com.mr3y.podcaster.ui.components.Error
import com.mr3y.podcaster.ui.components.LoadingIndicator
import com.mr3y.podcaster.ui.components.PlayPauseCompactButton
import com.mr3y.podcaster.ui.components.PullToRefresh
import com.mr3y.podcaster.ui.components.rememberHtmlToAnnotatedString
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.RefreshResult
import com.mr3y.podcaster.ui.presenter.podcastdetails.PodcastDetailsUIState
import com.mr3y.podcaster.ui.presenter.podcastdetails.PodcastDetailsViewModel
import com.mr3y.podcaster.ui.presenter.podcastdetails.SubscriptionState
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.Episodes
import com.mr3y.podcaster.ui.preview.PodcastWithDetails
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.onPrimaryTertiaryContainer
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiaryContainer

@Composable
fun PodcastDetailsScreen(
    onNavigateUp: () -> Unit,
    onEpisodeClick: (episodeId: Long, podcastArtworkUrl: String) -> Unit,
    appState: PodcasterAppState,
    contentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
    viewModel: PodcastDetailsViewModel = hiltViewModel()
) {
    val podcastDetailsState by viewModel.state.collectAsStateWithLifecycle()
    val currentlyPlayingEpisode by appState.currentlyPlayingEpisode.collectAsStateWithLifecycle()
    PodcastDetailsScreen(
        state = podcastDetailsState,
        onNavigateUp = onNavigateUp,
        onSubscribe = viewModel::subscribe,
        onUnsubscribe = viewModel::unsubscribe,
        onRefresh = viewModel::refresh,
        onPlayEpisode = appState::play,
        onPause = appState::pause,
        currentlyPlayingEpisode = currentlyPlayingEpisode,
        onConsumeErrorPlayingStatus = appState::consumeErrorPlayingStatus,
        externalContentPadding = contentPadding,
        excludedWindowInsets = excludedWindowInsets,
        onConsumeResult = viewModel::consumeRefreshResult,
        onRetry = viewModel::retry,
        onEpisodeClick = onEpisodeClick,
        modifier = modifier
    )
}

@Composable
fun PodcastDetailsScreen(
    state: PodcastDetailsUIState,
    onNavigateUp: () -> Unit,
    onSubscribe: () -> Unit,
    onUnsubscribe: () -> Unit,
    onRefresh: () -> Unit,
    onPlayEpisode: (Episode) -> Unit,
    onPause: () -> Unit,
    currentlyPlayingEpisode: CurrentlyPlayingEpisode?,
    onConsumeErrorPlayingStatus: () -> Unit,
    externalContentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    onConsumeResult: () -> Unit,
    onRetry: () -> Unit,
    onEpisodeClick: (episodeId: Long, podcastArtworkUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val strings = LocalStrings.current
    val playingStatus = currentlyPlayingEpisode?.playingStatus
    LaunchedEffect(state.refreshResult, playingStatus) {
        when(state.refreshResult) {
            is RefreshResult.Error -> {
                snackBarHostState.showSnackbar(
                    message = strings.podcast_details_refresh_result_error
                )
                onConsumeResult()
            }
            is RefreshResult.Mixed -> {
                snackBarHostState.showSnackbar(
                    message = strings.podcast_details_refresh_result_mixed
                )
                onConsumeResult()
            }
            is RefreshResult.Ok, null -> {}
        }
        when(playingStatus) {
            PlayingStatus.Error -> {
                snackBarHostState.showSnackbar(
                    message = strings.generic_error_message
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
        }
    )
    var bitmap: ImageBitmap? by remember { mutableStateOf(null) }
    LaunchedEffect(bitmap) {
        val temp = bitmap
        if (temp != null) {
            dominantColorState.updateFrom(temp)
        }
    }
    PullToRefresh(
        isRefreshingDone = !state.isRefreshing,
        onRefresh = onRefresh
    ) {
        Scaffold(
            topBar = {
                PodcastDetailsTopBar(
                    containerColor = if (state.isPodcastLoading || state.podcast == null)
                        MaterialTheme.colorScheme.surface
                    else
                        dominantColorState.color,
                    navigationIconContentColor = if (state.isPodcastLoading || state.podcast == null)
                        MaterialTheme.colorScheme.onSurface
                    else
                        dominantColorState.onColor,
                    onNavigateUp = onNavigateUp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            snackbarHost = { SnackbarHost(snackBarHostState, Modifier.padding(externalContentPadding)) },
            contentWindowInsets = if (excludedWindowInsets != null) ScaffoldDefaults.contentWindowInsets.exclude(excludedWindowInsets) else ScaffoldDefaults.contentWindowInsets,
            modifier = modifier
        ) { contentPadding ->
            Box(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                when {
                    state.isPodcastLoading -> {
                        LoadingIndicator(modifier = Modifier.fillMaxSize())
                    }
                    state.podcast == null -> {
                        Error(onRetry = onRetry, modifier = Modifier.fillMaxSize())
                    }
                    else -> {
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
                            onSubscribe = onSubscribe,
                            onUnsubscribe = onUnsubscribe
                        )
                        LazyColumn(
                            modifier = Modifier
                                .padding(top = 96.dp)
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .padding(top = 64.dp)
                                .zIndex(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = externalContentPadding
                        ) {
                            item {
                                val urlHandler = LocalUriHandler.current
                                Metadata(
                                    podcast = state.podcast,
                                    onUrlClick = { url -> urlHandler.openUri(url) }
                                )
                            }

                            when {
                                state.isEpisodesLoading -> {
                                    item {
                                        LoadingIndicator(
                                            modifier = Modifier
                                                .fillParentMaxWidth()
                                                .height(144.dp)
                                        )
                                    }
                                }
                                state.episodes == null -> {
                                    item {
                                        Error(
                                            onRetry = onRetry,
                                            modifier = Modifier
                                                .fillParentMaxWidth()
                                                .height(144.dp)
                                        )
                                    }
                                }
                                else -> {
                                    itemsIndexed(
                                        state.episodes,
                                        key = { _, episode -> episode.id }
                                    ) { index, episode ->
                                        Episode(
                                            episode = episode,
                                            onEpisodeClick = { episodeId -> onEpisodeClick(episodeId, state.podcast.artworkUrl) },
                                            currentlyPlayingEpisode = currentlyPlayingEpisode,
                                            onPlay = onPlayEpisode,
                                            onPause = onPause
                                        )
                                        if (index != state.episodes.lastIndex) {
                                            HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PodcastDetailsTopBar(
    containerColor: Color,
    navigationIconContentColor: Color,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Transparent, contentColor = navigationIconContentColor)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = strings.icon_navigate_up_content_description,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            navigationIconContentColor = navigationIconContentColor
        ),
        title = { },
        actions = {},
        modifier = modifier
    )
}

@Composable
private fun BoxScope.Header(
    artworkUrl: String,
    onState: ((AsyncImagePainter.State) -> Unit)?,
    dominantColor: Color,
    subscriptionState: SubscriptionState,
    isSubscriptionInEditMode: Boolean,
    onSubscribe: () -> Unit,
    onUnsubscribe: () -> Unit
) {
    val context = LocalContext.current
    Box(
        Modifier
            .height(96.dp)
            .background(dominantColor)
            .fillMaxWidth()
    )

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(artworkUrl)
            .size(128)
            .scale(Scale.FILL)
            .allowHardware(false)
            .memoryCacheKey("$artworkUrl.palette")
            .build(),
        onState = onState,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp)
            .size(128.dp)
            .border(2.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
            .zIndex(3f)
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
        horizontalArrangement = Arrangement.End
    ) {
        val strings = LocalStrings.current
        AnimatedContent(
            targetState = subscriptionState,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                        scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)))
                    .togetherWith(fadeOut(animationSpec = tween(90)))
                    .using(SizeTransform(clip = false, sizeAnimationSpec = { _,_ -> tween(600) }))
            },
            label = "Toggle Subscription state",
            modifier = Modifier.padding(vertical = 4.dp)
        ) { targetState ->
            when(targetState) {
                SubscriptionState.Subscribed -> {
                    OutlinedButton(
                        onClick = onUnsubscribe,
                        enabled = !isSubscriptionInEditMode,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primaryTertiary
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryTertiary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.unsubscribe_label,
                            style = MaterialTheme.typography.labelLarge
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
                            contentColor = MaterialTheme.colorScheme.onPrimaryTertiary
                        )
                    ) {
                        Text(
                            text = strings.subscribe_label,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LazyItemScope.Metadata(
    podcast: Podcast,
    onUrlClick: (url: String) -> Unit
) {
    Text(
        text = podcast.title,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = podcast.author,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(
            onClick = { onUrlClick(podcast.website) },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.inverseSurface
            ),
            modifier = Modifier.requiredSize(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.world_wide_web),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.inverseSurface
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        podcast.genres.forEach { genre ->
            SuggestionChip(
                onClick = { },
                label = {
                    Text(text = genre.label)
                }
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    val strings = LocalStrings.current
    Text(
        text = strings.about_label,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = podcast.description,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(modifier = Modifier.height(24.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = strings.episodes_label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = podcast.episodeCount.toString(),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LazyItemScope.Episode(
    episode: Episode,
    onEpisodeClick: (episodeId: Long) -> Unit,
    currentlyPlayingEpisode: CurrentlyPlayingEpisode?,
    onPlay: (Episode) -> Unit,
    onPause: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onEpisodeClick(episode.id) })
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = episode.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = rememberHtmlToAnnotatedString(text = episode.description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            PlayPauseCompactButton(
                isSelected = currentlyPlayingEpisode != null && currentlyPlayingEpisode.episode.id == episode.id,
                playingStatus = currentlyPlayingEpisode?.playingStatus,
                onPlay = { onPlay(episode) },
                onPause = onPause
            )
            DownloadButton(
                onDownload = { /*TODO*/ },
                onCancelDownload = { /*TODO*/ }
            )
        }
    }
}

@PodcasterPreview
@Composable
fun PodcastDetailsScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean
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
                refreshResult = null
            ),
            onNavigateUp = {},
            onSubscribe = {},
            onUnsubscribe = {},
            onRefresh = {},
            onPlayEpisode = {},
            onPause = {},
            currentlyPlayingEpisode = null,
            onConsumeErrorPlayingStatus = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            onConsumeResult = {},
            onRetry = {},
            onEpisodeClick = { _, _ -> },
            modifier = Modifier.fillMaxSize()
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
                refreshResult = null
            ),
            onNavigateUp = {},
            onSubscribe = {},
            onUnsubscribe = {},
            onRefresh = {},
            onPlayEpisode = {},
            onPause = {},
            currentlyPlayingEpisode = null,
            onConsumeErrorPlayingStatus = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            onConsumeResult = {},
            onRetry = {},
            onEpisodeClick = { _, _ -> },
            modifier = Modifier.fillMaxSize()
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
                refreshResult = null
            ),
            onNavigateUp = {},
            onSubscribe = {},
            onUnsubscribe = {},
            onRefresh = {},
            onPlayEpisode = {},
            onPause = {},
            currentlyPlayingEpisode = null,
            onConsumeErrorPlayingStatus = {},
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            onConsumeResult = {},
            onRetry = {},
            onEpisodeClick = { _, _ -> },
            modifier = Modifier.fillMaxSize()
        )
    }
}
