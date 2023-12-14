package com.mr3y.podcaster.ui.screens

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
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
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mr3y.podcaster.R
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.ui.components.Error
import com.mr3y.podcaster.ui.components.LoadingIndicator
import com.mr3y.podcaster.ui.components.PullToRefresh
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
import com.mr3y.podcaster.ui.theme.onTertiaryPrimary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiaryContainer
import com.mr3y.podcaster.ui.theme.tertiaryPrimary


@Composable
fun PodcastDetailsScreen(
    onNavigateUp: () -> Unit,
    onEpisodeClick: (episodeId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PodcastDetailsViewModel = hiltViewModel()
) {
    val podcastDetailsState by viewModel.state.collectAsStateWithLifecycle()
    PodcastDetailsScreen(
        state = podcastDetailsState,
        onNavigateUp = onNavigateUp,
        onSubscribe = viewModel::subscribe,
        onUnsubscribe = viewModel::unsubscribe,
        onRefresh = viewModel::refresh,
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
    onConsumeResult: () -> Unit,
    onRetry: () -> Unit,
    onEpisodeClick: (episodeId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.refreshResult) {
        when(state.refreshResult) {
            is RefreshResult.Error -> {
                snackBarHostState.showSnackbar(
                    message = "Something went wrong, refreshing failed!."
                )
                onConsumeResult()
            }
            is RefreshResult.Mixed -> {
                snackBarHostState.showSnackbar(
                    message = "Something went wrong."
                )
                onConsumeResult()
            }
            is RefreshResult.Ok, null -> {}
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
            snackbarHost = { SnackbarHost(snackBarHostState) },
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
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                        Episode(episode = episode, onEpisodeClick = onEpisodeClick)
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
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Transparent, contentColor = navigationIconContentColor)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
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
                            text = "Unsubscribe",
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
                            text = "Subscribe",
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
    Text(
        text = "About",
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
            text = "Episodes",
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
    onEpisodeClick: (episodeId: Long) -> Unit
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
                text = episode.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .padding(8.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryPrimary,
                    contentColor = MaterialTheme.colorScheme.onTertiaryPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                )
            }
            OutlinedIconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.tertiaryPrimary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiaryPrimary)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowDownward,
                    contentDescription = null,
                )
            }
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
                refreshResult = null
            ),
            onNavigateUp = {},
            onSubscribe = {},
            onUnsubscribe = {},
            onRefresh = {},
            onConsumeResult = {},
            onRetry = {},
            onEpisodeClick = {},
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
                refreshResult = null
            ),
            onNavigateUp = {},
            onSubscribe = {},
            onUnsubscribe = {},
            onRefresh = {},
            onConsumeResult = {},
            onRetry = {},
            onEpisodeClick = {},
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
                refreshResult = null
            ),
            onNavigateUp = {},
            onSubscribe = {},
            onUnsubscribe = {},
            onRefresh = {},
            onConsumeResult = {},
            onRetry = {},
            onEpisodeClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
