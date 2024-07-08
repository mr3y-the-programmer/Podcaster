package com.mr3y.podcaster.ui.presenter.podcastdetails

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.ui.presenter.BaseMoleculeViewModel
import com.mr3y.podcaster.ui.presenter.RefreshResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PodcastDetailsViewModel.Factory::class)
class PodcastDetailsViewModel @AssistedInject constructor(
    private val podcastsRepository: PodcastsRepository,
    @Assisted val podcastId: Long,
) : BaseMoleculeViewModel<PodcastDetailsUIEvent>() {

    val state = moleculeScope.launchMolecule(mode = RecompositionMode.ContextClock) {
        PodcastDetailsPresenter(
            podcastId = podcastId,
            repository = podcastsRepository,
            events = events,
        )
    }

    fun subscribe() {
        events.tryEmit(PodcastDetailsUIEvent.Subscribe)
    }

    fun unsubscribe() {
        events.tryEmit(PodcastDetailsUIEvent.UnSubscribe)
    }

    fun refresh() {
        events.tryEmit(PodcastDetailsUIEvent.Refresh)
    }

    fun consumeRefreshResult() {
        events.tryEmit(PodcastDetailsUIEvent.RefreshResultConsumed)
    }

    fun retry() {
        events.tryEmit(PodcastDetailsUIEvent.Retry)
    }

    @AssistedFactory
    interface Factory {
        fun create(podcastId: Long): PodcastDetailsViewModel
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal fun PodcastDetailsPresenter(
    podcastId: Long,
    repository: PodcastsRepository,
    events: Flow<PodcastDetailsUIEvent>,
): PodcastDetailsUIState {
    var isPodcastLoading by remember { mutableStateOf(true) }
    var isEpisodesLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var podcast: Podcast? by remember { mutableStateOf(null) }
    var episodes: List<Episode>? by remember { mutableStateOf(null) }
    var subscriptionState by remember { mutableStateOf(SubscriptionState.NotSubscribed) }
    var isSubscriptionStateInEditMode by remember { mutableStateOf(true) }
    var refreshResult: RefreshResult? by remember { mutableStateOf(null) }
    val queueEpisodesIds by repository.getQueueEpisodesIds().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        launch {
            podcast = repository.getPodcast(podcastId, false)
            isPodcastLoading = false
            val temp = podcast
            if (temp != null) {
                episodes = repository.getEpisodesForPodcast(temp.id, temp.title, temp.artworkUrl, false)
                isEpisodesLoading = false
            }
        }
        launch {
            repository.isPodcastFromSubscriptions(podcastId)
                .map { if (it) SubscriptionState.Subscribed else SubscriptionState.NotSubscribed }
                .collect { state ->
                    isSubscriptionStateInEditMode = false
                    subscriptionState = state
                }
        }
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is PodcastDetailsUIEvent.Subscribe -> {
                    // Assigned to temp local variables, so kotlin can smart cast to type automatically.
                    val temp1 = podcast
                    val temp2 = episodes
                    if (temp1 != null && temp2 != null) {
                        isSubscriptionStateInEditMode = true
                        repository.subscribeToPodcast(podcast = temp1, episodes = temp2)
                    }
                }
                is PodcastDetailsUIEvent.UnSubscribe -> {
                    val temp1 = podcast
                    if (temp1 != null) {
                        isSubscriptionStateInEditMode = true
                        repository.unSubscribeFromPodcast(podcastId = temp1.id)
                    }
                }
                is PodcastDetailsUIEvent.Refresh -> {
                    val temp1 = podcast
                    if (temp1 != null) {
                        isRefreshing = true
                        val result1 = repository.getPodcast(podcastId, true)
                        val result2 = repository.getEpisodesForPodcast(temp1.id, temp1.title, temp1.artworkUrl, true)

                        if (result1 != null) {
                            podcast = result1
                        }
                        if (result2 != null) {
                            episodes = result2
                        }
                        isRefreshing = false
                        refreshResult = when {
                            result1 != null && result2 != null -> RefreshResult.Ok
                            result1 == null && result2 == null -> RefreshResult.Error
                            else -> RefreshResult.Mixed
                        }
                    }
                }
                is PodcastDetailsUIEvent.RefreshResultConsumed -> refreshResult = null
                is PodcastDetailsUIEvent.Retry -> {
                    var temp1 = podcast
                    val temp2 = episodes

                    if (temp1 == null && temp2 == null) {
                        isPodcastLoading = true
                        podcast = repository.getPodcast(podcastId, false)
                        isPodcastLoading = false
                        temp1 = podcast
                        if (temp1 != null) {
                            isEpisodesLoading = true
                            episodes = repository.getEpisodesForPodcast(
                                temp1.id,
                                temp1.title,
                                temp1.artworkUrl,
                                false,
                            )
                            isEpisodesLoading = false
                        }
                    } else if (temp2 == null && temp1 != null) {
                        isEpisodesLoading = true
                        episodes = repository.getEpisodesForPodcast(
                            temp1.id,
                            temp1.title,
                            temp1.artworkUrl,
                            false,
                        )
                        isEpisodesLoading = false
                    }
                }
                else -> {}
            }
        }
    }

    return PodcastDetailsUIState(
        isPodcastLoading = isPodcastLoading,
        isEpisodesLoading = isEpisodesLoading,
        podcast = podcast,
        subscriptionState = subscriptionState,
        isSubscriptionStateInEditMode = isSubscriptionStateInEditMode,
        episodes = episodes,
        isRefreshing = isRefreshing,
        refreshResult = refreshResult,
        queueEpisodesIds = queueEpisodesIds,
    )
}
