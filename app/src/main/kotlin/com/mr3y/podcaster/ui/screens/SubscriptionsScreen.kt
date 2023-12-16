package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.ui.components.PullToRefresh
import com.mr3y.podcaster.ui.presenter.RefreshResult
import com.mr3y.podcaster.ui.presenter.subscriptions.SubscriptionsUIState
import com.mr3y.podcaster.ui.presenter.subscriptions.SubscriptionsViewModel
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.Episodes
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.preview.Podcasts
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.onTertiaryPrimary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.tertiaryPrimary

@Composable
fun SubscriptionsScreen(
    onPodcastClick: (podcastId: Long) -> Unit,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    onSettingsClick: () -> Unit,
    onNavDrawerClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val subscriptionsState by viewModel.state.collectAsStateWithLifecycle()
    SubscriptionsScreen(
        state = subscriptionsState,
        onPodcastClick = onPodcastClick,
        onEpisodeClick = onEpisodeClick,
        onSettingsClick = onSettingsClick,
        onNavDrawerClick = onNavDrawerClick,
        onRefresh = viewModel::refresh,
        onRefreshResultConsumed = viewModel::consumeRefreshResult,
        modifier = modifier
    )
}

@Composable
fun SubscriptionsScreen(
    state: SubscriptionsUIState,
    onPodcastClick: (podcastId: Long) -> Unit,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    onSettingsClick: () -> Unit,
    onNavDrawerClick: () -> Unit,
    onRefresh: () -> Unit,
    onRefreshResultConsumed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.refreshResult) {
        when(state.refreshResult) {
            is RefreshResult.Error -> {
                snackBarHostState.showSnackbar(
                    message = "Couldn't refresh feeds"
                )
                onRefreshResultConsumed()
            }
            is RefreshResult.Mixed -> {
                snackBarHostState.showSnackbar(
                    message = "Couldn't refresh some feeds"
                )
                onRefreshResultConsumed()
            }
            is RefreshResult.Ok, null -> {}
        }
    }
    PullToRefresh(
        onRefresh = onRefresh
    ) {
        Scaffold(
            topBar = {
                SubscriptionsTopAppBar(
                    onSettingsClick = onSettingsClick,
                    onNavDrawerClick = onNavDrawerClick,
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryTertiary),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SubscriptionsHeader(podcasts = state.subscriptions, onPodcastClick = onPodcastClick)
                    EpisodesList(episodes = state.episodes, onEpisodeClick = onEpisodeClick)
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
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onNavDrawerClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Tap to open Navigation drawer"
                )
            }
        },
        title = { },
        actions = {
            IconButton(
                onClick = onSettingsClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Tap to navigate to settings"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryTertiary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryTertiary
        ),
        modifier = modifier
    )
}

@Composable
private fun ColumnScope.SubscriptionsHeader(
    podcasts: List<Podcast>,
    onPodcastClick: (podcastId: Long) -> Unit
) {
    Text(
        text = "Subscriptions",
        color = MaterialTheme.colorScheme.onPrimaryTertiary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
    )
    if (podcasts.isNotEmpty()) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
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
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    } else {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "You aren't subscribed to any podcast.\nYour subscriptions will show up here.",
            color = MaterialTheme.colorScheme.onPrimaryTertiary,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .height(120.dp)
        )
    }
}

@Composable
private fun ColumnScope.EpisodesList(
    episodes: List<Episode>,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        if (episodes.isNotEmpty()) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(episodes, key = { _, episode -> episode.id }) { index, episode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { onEpisodeClick(episode.id, episode.artworkUrl) })
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        AsyncImage(
                            model = episode.artworkUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.FillBounds
                        )
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
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryPrimary, contentColor = MaterialTheme.colorScheme.onTertiaryPrimary)
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
                                colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.tertiaryPrimary),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiaryPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowDownward,
                                    contentDescription = null,
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
            Spacer(modifier = Modifier.height(80.dp))
            Text(
                text = "Start subscribing podcasts by clicking on ☰ icon -> then Explore.",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@PodcasterPreview
@Composable
fun SubscriptionsScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        val state by remember {
            mutableStateOf(
                SubscriptionsUIState(
                    refreshResult = null,
                    subscriptions = Podcasts,
                    episodes = Episodes
                )
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
            modifier = Modifier.fillMaxSize()
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
                    refreshResult = null,
                    subscriptions = emptyList(),
                    episodes = emptyList()
                )
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
            modifier = Modifier.fillMaxSize()
        )
    }
}
