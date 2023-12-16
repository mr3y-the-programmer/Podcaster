package com.mr3y.podcaster.ui.presenter.subscriptions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.ui.presenter.RefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository
): ViewModel() {

    private val events = MutableSharedFlow<SubscriptionsUIEvent>(extraBufferCapacity = 20)

    private val moleculeScope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    val state = moleculeScope.launchMolecule(mode = RecompositionMode.ContextClock) {
        SubscriptionsPresenter(repository = podcastsRepository, events = events)
    }

    fun refresh() {
        events.tryEmit(SubscriptionsUIEvent.Refresh)
    }

    fun consumeRefreshResult() {
        events.tryEmit(SubscriptionsUIEvent.RefreshResultConsumed)
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal fun SubscriptionsPresenter(
    repository: PodcastsRepository,
    events: Flow<SubscriptionsUIEvent>
): SubscriptionsUIState {
    val podcasts by repository.getSubscriptions().collectAsState(initial = emptyList())
    val episodes by repository.getEpisodesForPodcasts(podcasts.map { it.id }.toSet(), limit = 200).collectAsState(initial = emptyList())
    var refreshResult: RefreshResult? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when(event) {
                is SubscriptionsUIEvent.Refresh -> {
                    val aggregatedRefreshResults = podcasts.map { podcast ->
                        async {
                            val result1 = repository.refreshPodcast(podcast.id)
                            val result2 = repository.refreshEpisodesForPodcast(podcast.id, podcast.title, podcast.artworkUrl)
                            result1 && result2
                        }
                    }.awaitAll()

                    refreshResult = when {
                        aggregatedRefreshResults.all { isSuccessful -> isSuccessful } -> RefreshResult.Ok
                        !aggregatedRefreshResults.any { isSuccessful -> isSuccessful } -> RefreshResult.Error
                        else -> RefreshResult.Mixed
                    }
                }
                is SubscriptionsUIEvent.RefreshResultConsumed -> refreshResult = null
            }
        }
    }

    return SubscriptionsUIState(
        refreshResult = refreshResult,
        subscriptions = podcasts,
        episodes = episodes
    )
}