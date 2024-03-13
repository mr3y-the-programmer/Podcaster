package com.mr3y.podcaster.ui.presenter.subscriptions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.ui.presenter.BaseMoleculeViewModel
import com.mr3y.podcaster.ui.presenter.RefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
) : BaseMoleculeViewModel<SubscriptionsUIEvent>() {

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
    events: Flow<SubscriptionsUIEvent>,
): SubscriptionsUIState {
    var isSubscriptionsLoading by remember { mutableStateOf(true) }
    var isEpisodesLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val podcasts by repository.getSubscriptions().collectAsState(initial = emptyList())
    val episodes by repository.getEpisodesWithDownloadMetadataForPodcasts(podcasts.map { it.id }.toSet(), limit = 200).collectAsState(initial = emptyList())
    val queueEpisodesIds by repository.getQueueEpisodesIds().collectAsState(initial = emptyList())
    var refreshResult: RefreshResult? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        launch {
            // if the user has no subscriptions yet
            repository.hasSubscriptions()
                .filter { isSubscribedToAnyPodcast -> !isSubscribedToAnyPodcast }
                .collect {
                    isSubscriptionsLoading = false
                    isEpisodesLoading = false
                }
        }
        launch {
            snapshotFlow { podcasts }
                .drop(1) // Ignore initial value
                .collect {
                    isSubscriptionsLoading = false
                }
        }

        launch {
            snapshotFlow { episodes }
                .drop(1) // Ignore initial value
                .collect {
                    isEpisodesLoading = false
                }
        }
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is SubscriptionsUIEvent.Refresh -> {
                    isRefreshing = true
                    val aggregatedRefreshResults = podcasts.map { podcast ->
                        async {
                            val result1 = repository.syncRemotePodcastWithLocal(podcast.id)
                            val result2 = repository.syncRemoteEpisodesForPodcastWithLocal(podcast.id, podcast.title, podcast.artworkUrl)
                            result1 && result2
                        }
                    }.awaitAll()

                    isRefreshing = false
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
        isSubscriptionsLoading = isSubscriptionsLoading,
        isEpisodesLoading = isEpisodesLoading,
        isRefreshing = isRefreshing,
        subscriptions = podcasts,
        episodes = episodes,
        queueEpisodesIds = queueEpisodesIds,
        refreshResult = refreshResult,
    )
}
