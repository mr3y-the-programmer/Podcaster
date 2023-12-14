package com.mr3y.podcaster.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.ui.components.LoadingIndicator
import com.mr3y.podcaster.ui.presenter.explore.ExploreUIState
import com.mr3y.podcaster.ui.presenter.explore.ExploreViewModel
import com.mr3y.podcaster.ui.presenter.explore.SearchResult
import com.mr3y.podcaster.ui.preview.DynamicColorsParameterProvider
import com.mr3y.podcaster.ui.preview.PodcasterPreview
import com.mr3y.podcaster.ui.preview.Podcasts
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import com.mr3y.podcaster.ui.theme.onPrimaryTertiary
import com.mr3y.podcaster.ui.theme.primaryTertiary
import com.mr3y.podcaster.ui.theme.tertiaryPrimary

@Composable
fun ExploreScreen(
    onPodcastClick: (podcastId: Long) -> Unit,
    onNavDrawerClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val exploreState by viewModel.state.collectAsStateWithLifecycle()
    ExploreScreen(
        state = exploreState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onCommittingSearch = viewModel::search,
        onDeleteRecentSearchQuery = viewModel::deleteSearchQuery,
        onRetry = viewModel::retry,
        onConsumeResult = viewModel::consumeResult,
        onPodcastClick = onPodcastClick,
        onNavDrawerClick = onNavDrawerClick,
        modifier = modifier
    )
}

@Composable
fun ExploreScreen(
    state: ExploreUIState,
    onSearchQueryChange: (TextFieldValue) -> Unit,
    onCommittingSearch: () -> Unit,
    onDeleteRecentSearchQuery: (String) -> Unit,
    onRetry: () -> Unit,
    onConsumeResult: () -> Unit,
    onPodcastClick: (podcastId: Long) -> Unit,
    onNavDrawerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            ExploreTopAppBar(
                onNavDrawerClick = onNavDrawerClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(end = 16.dp)
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) { contentPadding ->
        val focusManager = LocalFocusManager.current
        val searchBarInteractionSource = remember { MutableInteractionSource() }
        val isSearchBarFocused by searchBarInteractionSource.collectIsFocusedAsState()
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ) {
            ExploreSearchBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                interactionSource = searchBarInteractionSource,
                showConfirmButton = isSearchBarFocused && state.searchQuery.text.isNotBlank(),
                onConfirmButtonClick = {
                    onCommittingSearch()
                    focusManager.clearFocus()
                },
                modifier = Modifier.fillMaxWidth()
            )
            val recentSearches = state.previousSearchQueries
            RecentSearches(
                recentSearches = recentSearches,
                isVisible = isSearchBarFocused && recentSearches.isNotEmpty(),
                onSearchQueryClick = { recentSearchQuery ->
                    onSearchQueryChange(TextFieldValue(recentSearchQuery))
                    onCommittingSearch()
                    focusManager.clearFocus()
                },
                onDeleteSearchQuery = onDeleteRecentSearchQuery,
                onCloseClick = {
                    focusManager.clearFocus()
                }
            )
            if (!isSearchBarFocused) {
                val contentModifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                if (state.searchResult is SearchResult.Loading) {
                    LoadingIndicator(modifier = contentModifier)
                }
                if (state.searchResult is SearchResult.SearchByTermSuccess) {
                    PodcastsList(
                        podcasts = state.searchResult.podcasts,
                        onPodcastClick = onPodcastClick,
                        modifier = contentModifier
                    )
                }
                if (state.searchResult is SearchResult.Error && !state.searchResult.isFeedUrl) {
                    Error(onRetry = onRetry, modifier = contentModifier)
                }
                LaunchedEffect(state.searchResult) {
                    if (state.searchResult is SearchResult.SearchByUrlSuccess) {
                        onConsumeResult()
                        onPodcastClick(state.searchResult.podcast.id)
                    }
                    if (state.searchResult is SearchResult.Error && state.searchResult.isFeedUrl) {
                        snackBarHostState.showSnackbar("Make sure the feed url is correct and you're connected to Internet.")
                        onConsumeResult()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreTopAppBar(
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
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        modifier = modifier
    )
}

@Composable
private fun ExploreSearchBar(
    searchQuery: TextFieldValue,
    onSearchQueryChange: (TextFieldValue) -> Unit,
    interactionSource: MutableInteractionSource,
    showConfirmButton: Boolean,
    onConfirmButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        leadingIcon = {
            IconButton(
                onClick = { },
                modifier = Modifier.clearAndSetSemantics { }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        },
        placeholder = {
            Text(text = "Search for a podcast or add RSS Url")
        },
        trailingIcon = {
            if (showConfirmButton) {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    VerticalDivider(
                        color = MaterialTheme.colorScheme.primaryTertiary
                    )
                    IconButton(
                        onClick = onConfirmButtonClick,
                        modifier = Modifier.clearAndSetSemantics { }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            }
        },
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onConfirmButtonClick()
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedPlaceholderColor = MaterialTheme.colorScheme.primaryTertiary,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.primaryTertiary,
            focusedBorderColor = MaterialTheme.colorScheme.primaryTertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primaryTertiary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primaryTertiary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primaryTertiary,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primaryTertiary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.primaryTertiary,
            disabledTrailingIconColor = MaterialTheme.colorScheme.primaryTertiary,
            errorTrailingIconColor = MaterialTheme.colorScheme.primaryTertiary,
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        modifier = modifier
    )
}

@Composable
private fun RecentSearches(
    recentSearches: List<String>,
    isVisible: Boolean,
    onSearchQueryClick: (String) -> Unit,
    onDeleteSearchQuery: (String) -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(),
        exit = slideOutVertically() + fadeOut(),
        label = "Animated Recent Searches",
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = "Recent Searches",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.alignByBaseline()
                )
                TextButton(
                    onClick = onCloseClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.inverseSurface
                    ),
                    modifier = Modifier.alignByBaseline()
                ) {
                    Text(
                        text = "CLOSE",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            repeat(recentSearches.size) { index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clickable(
                            role = Role.Button,
                            onClick = { onSearchQueryClick(recentSearches[index]) }
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.SavedSearch,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recentSearches[index],
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { onDeleteSearchQuery(recentSearches[index]) },
                        modifier = Modifier
                            .size(48.dp)
                            .padding(8.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.tertiaryPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                        )
                    }
                }
                if (index != recentSearches.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun PodcastsList(
    podcasts: List<Podcast>,
    onPodcastClick: (podcastId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (podcasts.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {
            Text(
                text = "No podcasts found matching your search.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                podcasts,
                key = { _, podcast -> podcast.id }
            ) { index, podcast ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .clickable(onClick = { onPodcastClick(podcast.id) })
                ) {
                    AsyncImage(
                        model = podcast.artworkUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = podcast.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = podcast.author,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            text = podcast.description,
                            color = MaterialTheme.colorScheme.inverseSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (index != podcasts.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun Error(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "Sorry, Something went wrong",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedButton(
            onClick = onRetry,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryTertiary,
                contentColor = MaterialTheme.colorScheme.onPrimaryTertiary
            ),
        ) {
            Text(
                text = "Retry",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@PodcasterPreview
@Composable
fun ExploreScreenInitialPreview(
    @PreviewParameter(DynamicColorsParameterProvider::class) isDynamicColorsOn: Boolean
) {
    PodcasterTheme(dynamicColor = isDynamicColorsOn) {
        val state by remember {
            mutableStateOf(
                ExploreUIState(
                    searchQuery = TextFieldValue(""),
                    searchResult = null,
                    previousSearchQueries = listOf("android", "podcast", "culture", "tech")
                )
            )
        }
        ExploreScreen(
            state = state,
            onSearchQueryChange = {},
            onCommittingSearch = {},
            onPodcastClick = {},
            onNavDrawerClick = {},
            onRetry = {},
            onDeleteRecentSearchQuery = {},
            onConsumeResult = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@PodcasterPreview
@Composable
fun ExploreScreenPodcastsListPreview() {
    PodcasterTheme(dynamicColor = false) {
        val state by remember {
            mutableStateOf(
                ExploreUIState(
                    searchQuery = TextFieldValue("podc"),
                    searchResult = SearchResult.SearchByTermSuccess(Podcasts),
                    previousSearchQueries = listOf("android", "podcast", "culture", "tech")
                )
            )
        }
        ExploreScreen(
            state = state,
            onSearchQueryChange = {},
            onCommittingSearch = {},
            onPodcastClick = {},
            onNavDrawerClick = {},
            onRetry = {},
            onDeleteRecentSearchQuery = {},
            onConsumeResult = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@PodcasterPreview
@Composable
fun ExploreScreenErrorPreview() {
    PodcasterTheme(dynamicColor = false) {
        val state by remember {
            mutableStateOf(
                ExploreUIState(
                    searchQuery = TextFieldValue("podc"),
                    searchResult = SearchResult.Error(errorResponse = Unit, isFeedUrl = false),
                    previousSearchQueries = listOf("android", "podcast", "culture", "tech")
                )
            )
        }
        ExploreScreen(
            state = state,
            onSearchQueryChange = {},
            onCommittingSearch = {},
            onPodcastClick = {},
            onNavDrawerClick = {},
            onRetry = {},
            onDeleteRecentSearchQuery = {},
            onConsumeResult = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
