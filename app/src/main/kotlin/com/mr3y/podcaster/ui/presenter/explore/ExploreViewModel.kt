package com.mr3y.podcaster.ui.presenter.explore

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.github.michaelbull.result.mapBoth
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.ui.presenter.BaseMoleculeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
) : BaseMoleculeViewModel<ExploreUIEvent>() {

    val state = moleculeScope.launchMolecule(mode = RecompositionMode.ContextClock) {
        ExplorePresenter(repository = podcastsRepository, events = events)
    }

    fun updateSearchQuery(newSearchQuery: TextFieldValue) {
        events.tryEmit(ExploreUIEvent.UpdateSearchQuery(newSearchQuery))
    }

    fun search() {
        events.tryEmit(ExploreUIEvent.Search)
    }

    fun deleteSearchQuery(searchQueryText: String) {
        events.tryEmit(ExploreUIEvent.DeleteSearchQuery(searchQueryText))
    }

    fun consumeResult() {
        events.tryEmit(ExploreUIEvent.ResultConsumed)
    }

    fun retry() {
        events.tryEmit(ExploreUIEvent.Retry)
    }
}

internal val FeedUrlRegex = """https?://(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&//=]*)""".toRegex()

@SuppressLint("ComposableNaming")
@Composable
internal fun ExplorePresenter(
    repository: PodcastsRepository,
    events: Flow<ExploreUIEvent>,
): ExploreUIState {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val recentSearchQueries by repository.getRecentSearchQueries().collectAsState(initial = emptyList())
    var searchResult: SearchResult? by remember { mutableStateOf(null) }

    suspend fun fetchPodcast(podcastUrl: String) {
        searchResult = SearchResult.Loading
        repository.getPodcast(podcastUrl)
            .mapBoth(
                success = { podcast ->
                    searchResult = SearchResult.SearchByUrlSuccess(podcast)
                },
                failure = { errorResponse ->
                    searchResult = SearchResult.Error(isFeedUrl = true, errorResponse)
                },
            )
    }

    suspend fun fetchPodcasts(searchTerm: String) {
        searchResult = SearchResult.Loading
        repository.saveNewSearchQuery(searchTerm)
        repository.searchForPodcastsByTerm(searchTerm)
            .mapBoth(
                success = { podcasts ->
                    searchResult = SearchResult.SearchByTermSuccess(podcasts)
                },
                failure = { errorResponse ->
                    searchResult = SearchResult.Error(isFeedUrl = false, errorResponse)
                },
            )
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is ExploreUIEvent.Search -> {
                    val searchText = searchQuery.text
                    when {
                        searchText.isBlank() -> {}
                        FeedUrlRegex.matches(searchText) -> {
                            fetchPodcast(searchText)
                        }
                        else -> {
                            fetchPodcasts(searchText)
                        }
                    }
                }
                is ExploreUIEvent.UpdateSearchQuery -> {
                    searchQuery = event.newSearchQuery
                }
                is ExploreUIEvent.DeleteSearchQuery -> {
                    repository.deleteSearchQuery(event.searchQuery)
                }
                is ExploreUIEvent.Retry -> {
                    if (searchResult is SearchResult.Error) {
                        val searchText = searchQuery.text
                        when {
                            searchText.isBlank() -> {}
                            FeedUrlRegex.matches(searchText) -> {
                                fetchPodcast(searchText)
                            }
                            else -> {
                                fetchPodcasts(searchText)
                            }
                        }
                    }
                }
                is ExploreUIEvent.ResultConsumed -> {
                    searchResult = null
                }
            }
        }
    }

    return ExploreUIState(
        searchQuery = searchQuery,
        searchResult = searchResult,
        previousSearchQueries = recentSearchQueries,
    )
}
