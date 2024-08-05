package com.mr3y.podcaster.ui.presenter.episodedetails

import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
import com.mr3y.podcaster.ui.presenter.RefreshResult

data class EpisodeDetailsUIState(
    val isLoading: Boolean,
    val episode: Episode?,
    val queueEpisodesIds: List<Long>,
    val isRefreshing: Boolean,
    val refreshResult: RefreshResult?,
    val downloadMetadata: EpisodeDownloadMetadata?,
)

sealed interface EpisodeDetailsUIEvent {

    data object Refresh : EpisodeDetailsUIEvent

    data object RefreshResultConsumed : EpisodeDetailsUIEvent

    data object Retry : EpisodeDetailsUIEvent

    data class PlayEpisode(val episode: Episode) : EpisodeDetailsUIEvent

    data object Pause : EpisodeDetailsUIEvent

    data class DownloadEpisode(val episode: Episode) : EpisodeDetailsUIEvent

    data class ResumeDownloading(val episodeId: Long) : EpisodeDetailsUIEvent

    data class PauseDownloading(val episodeId: Long) : EpisodeDetailsUIEvent

    data class AddEpisodeToQueue(val episode: Episode) : EpisodeDetailsUIEvent

    data class RemoveEpisodeFromQueue(val episodeId: Long) : EpisodeDetailsUIEvent

    data class ToggleEpisodeFavoriteStatus(val isFavorite: Boolean) : EpisodeDetailsUIEvent

    data object ErrorPlayingStatusConsumed : EpisodeDetailsUIEvent
}
