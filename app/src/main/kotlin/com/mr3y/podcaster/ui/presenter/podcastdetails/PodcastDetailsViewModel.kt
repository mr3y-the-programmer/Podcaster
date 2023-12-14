package com.mr3y.podcaster.ui.presenter.podcastdetails

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.kiwi.navigationcompose.typed.decodeArguments
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.ui.navigation.Destinations
import com.mr3y.podcaster.ui.presenter.RefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
@HiltViewModel
class PodcastDetailsViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val events = MutableSharedFlow<PodcastDetailsUIEvent>(extraBufferCapacity = 20)

    private val moleculeScope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    private val navArguments = savedStateHandle.decodeArguments<Destinations.PodcastDetails>()

    val state = moleculeScope.launchMolecule(mode = RecompositionMode.ContextClock) {
        PodcastDetailsPresenter(
            podcastId = navArguments.podcastId,
            repository = podcastsRepository,
            events = events
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
}

@SuppressLint("ComposableNaming")
@Composable
internal fun PodcastDetailsPresenter(
    podcastId: Long,
    repository: PodcastsRepository,
    events: Flow<PodcastDetailsUIEvent>
): PodcastDetailsUIState {
    var isPodcastLoading by remember { mutableStateOf(true) }
    var isEpisodesLoading by remember { mutableStateOf(true) }
    var podcast: Podcast? by remember { mutableStateOf(null) }
    var episodes: List<Episode>? by remember { mutableStateOf(null) }
    var subscriptionState by remember { mutableStateOf(SubscriptionState.NotSubscribed) }
    var isSubscriptionStateInEditMode by remember { mutableStateOf(true) }
    var refreshResult: RefreshResult? by remember { mutableStateOf(null) }

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
            when(event) {
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
                        val result1 = repository.getPodcast(podcastId, true)
                        val result2 = repository.getEpisodesForPodcast(temp1.id, temp1.title, temp1.artworkUrl, true)

                        if (result1 != null) {
                            podcast = result1
                        }
                        if (result2 != null) {
                            episodes = result2
                        }
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
                                false
                            )
                            isEpisodesLoading = false
                        }
                    } else if (temp2 == null && temp1 != null) {
                        isEpisodesLoading = true
                        episodes = repository.getEpisodesForPodcast(
                            temp1.id,
                            temp1.title,
                            temp1.artworkUrl,
                            false
                        )
                        isEpisodesLoading = false
                    }
                }
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
        refreshResult = refreshResult
    )
}
