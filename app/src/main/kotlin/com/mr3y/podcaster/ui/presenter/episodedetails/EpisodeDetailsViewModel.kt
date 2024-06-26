package com.mr3y.podcaster.ui.presenter.episodedetails

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.kiwi.navigationcompose.typed.decodeArguments
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.ui.navigation.Destinations
import com.mr3y.podcaster.ui.presenter.BaseMoleculeViewModel
import com.mr3y.podcaster.ui.presenter.RefreshResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class EpisodeDetailsViewModel @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseMoleculeViewModel<EpisodeDetailsUIEvent>() {

    private val navArguments = savedStateHandle.decodeArguments<Destinations.EpisodeDetails>()

    val state = moleculeScope.launchMolecule(mode = RecompositionMode.ContextClock) {
        EpisodeDetailsPresenter(
            episodeId = navArguments.episodeId,
            podcastArtworkUrl = navArguments.podcastArtworkUrl,
            repository = podcastsRepository,
            events = events,
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
    events: Flow<EpisodeDetailsUIEvent>,
): EpisodeDetailsUIState {
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var episode: Episode? by remember { mutableStateOf(null) }
    val downloadMetadata by repository.getEpisodeDownloadMetadata(episodeId).collectAsState(initial = null)
    val queueEpisodesIds by repository.getQueueEpisodesIds().collectAsState(initial = emptyList())
    var refreshResult: RefreshResult? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        episode = repository.getEpisode(episodeId = episodeId, podcastArtworkUrl = podcastArtworkUrl, forceRefresh = false)
        isLoading = false
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
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
                        isRefreshing = true
                        val result = repository.getEpisode(episodeId, podcastArtworkUrl, true)

                        if (result != null) {
                            episode = result
                        }
                        isRefreshing = false
                        refreshResult = if (result != null) RefreshResult.Ok else RefreshResult.Error
                    }
                }
                EpisodeDetailsUIEvent.RefreshResultConsumed -> refreshResult = null
                else -> {}
            }
        }
    }

    return EpisodeDetailsUIState(
        isLoading = isLoading,
        episode = episode,
        queueEpisodesIds = queueEpisodesIds,
        isRefreshing = isRefreshing,
        refreshResult = refreshResult,
        downloadMetadata = downloadMetadata,
    )
}
