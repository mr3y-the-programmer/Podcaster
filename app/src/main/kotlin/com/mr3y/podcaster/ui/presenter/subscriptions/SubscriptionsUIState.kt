package com.mr3y.podcaster.ui.presenter.subscriptions

import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeWithDownloadMetadata
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.ui.presenter.RefreshResult

data class SubscriptionsUIState(
    val isSubscriptionsLoading: Boolean,
    val isEpisodesLoading: Boolean,
    val isRefreshing: Boolean,
    val refreshResult: RefreshResult?,
    val subscriptions: List<Podcast>,
    val episodes: List<EpisodeWithDownloadMetadata>,
    val queueEpisodesIds: List<Long>,
)

sealed interface SubscriptionsUIEvent {

    data object Refresh : SubscriptionsUIEvent

    data object RefreshResultConsumed : SubscriptionsUIEvent

    data class ToggleAppTheme(val isDark: Boolean) : SubscriptionsUIEvent

    data class PlayEpisode(val episode: Episode) : SubscriptionsUIEvent

    data object Pause : SubscriptionsUIEvent

    data class AddEpisodeToQueue(val episode: Episode) : SubscriptionsUIEvent

    data class RemoveEpisodeFromQueue(val episodeId: Long) : SubscriptionsUIEvent

    data object ErrorPlayingStatusConsumed : SubscriptionsUIEvent
}
