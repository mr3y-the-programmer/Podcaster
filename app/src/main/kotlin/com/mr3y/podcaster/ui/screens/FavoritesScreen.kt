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
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.sampledata.Episodes
import com.mr3y.podcaster.ui.components.CoilImage
import com.mr3y.podcaster.ui.components.LoadingIndicator
import com.mr3y.podcaster.ui.components.LocalAnimatedVisibilityScope
import com.mr3y.podcaster.ui.components.LocalSharedTransitionScope
import com.mr3y.podcaster.ui.components.TopBar
import com.mr3y.podcaster.ui.components.animateEnterExit
import com.mr3y.podcaster.ui.components.rememberHtmlToAnnotatedString
import com.mr3y.podcaster.ui.components.rememberSharedContentState
import com.mr3y.podcaster.ui.components.renderInSharedTransitionScopeOverlay
import com.mr3y.podcaster.ui.components.sharedElement
import com.mr3y.podcaster.ui.presenter.favorites.FavoritesUIState
import com.mr3y.podcaster.ui.presenter.favorites.FavoritesViewModel
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.isAppThemeDark
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight

@Composable
fun FavoritesScreen(
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    onNavigateUp: () -> Unit,
    contentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FavoritesScreen(
        state = state,
        onEpisodeClick = onEpisodeClick,
        onNavigateUp = onNavigateUp,
        externalContentPadding = contentPadding,
        excludedWindowInsets = excludedWindowInsets,
        modifier = modifier,
    )
}

@Composable
fun FavoritesScreen(
    state: FavoritesUIState,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    onNavigateUp: () -> Unit,
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
                onUpButtonClick = onNavigateUp,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
                modifier = Modifier
                    .renderInSharedTransitionScopeOverlay(
                        LocalSharedTransitionScope.current,
                        zIndexInOverlay = 1f,
                    )
                    .animateEnterExit(LocalAnimatedVisibilityScope.current)
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
            FavoriteList(
                favorites = state.favorites,
                onEpisodeClick = onEpisodeClick,
                externalContentPadding = externalContentPadding,
                modifier = contentModifier,
            )
        }
    }
}

@Composable
private fun FavoriteList(
    favorites: List<Episode>,
    onEpisodeClick: (episodeId: Long, artworkUrl: String) -> Unit,
    externalContentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    if (favorites.isEmpty()) {
        EmptyFavorites(modifier)
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = externalContentPadding,
        ) {
            itemsIndexed(
                favorites,
                key = { _, episode -> episode.id },
            ) { index, episode ->
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
                        CoilImage(
                            artworkUrl = episode.artworkUrl,
                            sharedTransitionKey = episode.id.toString(),
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(64.dp)
                                .aspectRatio(1f)
                                .sharedElement(
                                    LocalSharedTransitionScope.current,
                                    LocalAnimatedVisibilityScope.current,
                                    rememberSharedContentState(key = episode.id.toString()),
                                )
                                .clip(RoundedCornerShape(8.dp)),
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
                }
                if (index != favorites.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun EmptyFavorites(
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Text(
            text = strings.favorites_empty_list,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@PodcasterPreview
@Composable
fun FavoritesScreenPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean,
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        FavoritesScreen(
            state = FavoritesUIState(isLoading = false, Episodes.slice(0..2)),
            onEpisodeClick = { _, _ -> },
            onNavigateUp = { },
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@PodcasterPreview
@Composable
fun FavoritesScreenEmptyFavoritesPreview() {
    PodcasterTheme(dynamicColor = false) {
        FavoritesScreen(
            state = FavoritesUIState(isLoading = false, emptyList()),
            onEpisodeClick = { _, _ -> },
            onNavigateUp = { },
            externalContentPadding = PaddingValues(0.dp),
            excludedWindowInsets = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
