package com.mr3y.podcaster.ui.presenter.episodedetails

import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
import com.mr3y.podcaster.ui.presenter.RefreshResult

data class EpisodeDetailsUIState(
    val isLoading: Boolean,
    val episode: Episode?,
    val isRefreshing: Boolean,
    val refreshResult: RefreshResult?,
    val downloadMetadata: EpisodeDownloadMetadata?,
)

sealed interface EpisodeDetailsUIEvent {

    data object Refresh : EpisodeDetailsUIEvent

    data object RefreshResultConsumed : EpisodeDetailsUIEvent

    data object Retry : EpisodeDetailsUIEvent
}
