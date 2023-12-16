package com.mr3y.podcaster.ui.presenter.episodedetails

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
import com.mr3y.podcaster.ui.navigation.Destinations
import com.mr3y.podcaster.ui.presenter.RefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
@HiltViewModel
class EpisodeDetailsViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val events = MutableSharedFlow<EpisodeDetailsUIEvent>(extraBufferCapacity = 20)

    private val moleculeScope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)

    private val navArguments = savedStateHandle.decodeArguments<Destinations.EpisodeDetails>()

    val state = moleculeScope.launchMolecule(mode = RecompositionMode.ContextClock) {
        EpisodeDetailsPresenter(
            episodeId = navArguments.episodeId,
            podcastArtworkUrl = navArguments.podcastArtworkUrl,
            repository = podcastsRepository,
            events = events
        )
    }

    fun refresh() {
        events.tryEmit(EpisodeDetailsUIEvent.Refresh)
    }

    fun consumeRefreshResult() {
        events.tryEmit(EpisodeDetailsUIEvent.RefreshResultConsumed)
    }

    fun retry() {
        events.tryEmit(EpisodeDetailsUIEvent.Retry)
    }
}

@SuppressLint("ComposableNaming")
@Composable
internal fun EpisodeDetailsPresenter(
    episodeId: Long,
    podcastArtworkUrl: String,
    repository: PodcastsRepository,
    events: Flow<EpisodeDetailsUIEvent>
): EpisodeDetailsUIState {
    var isLoading by remember { mutableStateOf(true) }
    var episode: Episode? by remember { mutableStateOf(null) }
    var refreshResult: RefreshResult? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        episode = repository.getEpisode(episodeId = episodeId, podcastArtworkUrl = podcastArtworkUrl, forceRefresh = false)
        isLoading = false
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when(event) {
                EpisodeDetailsUIEvent.Retry -> {
                    val temp = episode
                    if (temp == null) {
                        isLoading = true
                        episode = repository.getEpisode(episodeId, podcastArtworkUrl, false)
                        isLoading = false
                    }
                }
                EpisodeDetailsUIEvent.Refresh -> {
                    val temp = episode
                    if (temp != null) {
                        val result = repository.getEpisode(episodeId, podcastArtworkUrl, true)

                        if (result != null) {
                            episode = result
                        }
                        refreshResult = if (result != null) RefreshResult.Ok else RefreshResult.Error
                    }
                }
                EpisodeDetailsUIEvent.RefreshResultConsumed -> refreshResult = null
            }
        }
    }

    return EpisodeDetailsUIState(
        isLoading = isLoading,
        episode = episode,
        refreshResult = refreshResult,
    )
}
