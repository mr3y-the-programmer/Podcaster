package com.mr3y.podcaster.ui.presenter.podcastdetails

import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.ui.presenter.RefreshResult

data class PodcastDetailsUIState(
    val isPodcastLoading: Boolean,
    val isEpisodesLoading: Boolean,
    val podcast: Podcast?,
    val subscriptionState: SubscriptionState,
    val isSubscriptionStateInEditMode: Boolean,
    val episodes: List<Episode>?,
    val queueEpisodesIds: List<Long>,
    val isRefreshing: Boolean,
    val refreshResult: RefreshResult?,
)

enum class SubscriptionState {
    Subscribed,
    NotSubscribed,
}

sealed interface PodcastDetailsUIEvent {

    data object Subscribe : PodcastDetailsUIEvent

    data object UnSubscribe : PodcastDetailsUIEvent

    data object Refresh : PodcastDetailsUIEvent

    data object RefreshResultConsumed : PodcastDetailsUIEvent

    data object Retry : PodcastDetailsUIEvent

    data class PlayEpisode(val episode: Episode) : PodcastDetailsUIEvent

    data object Pause : PodcastDetailsUIEvent

    data class AddEpisodeToQueue(val episode: Episode) : PodcastDetailsUIEvent

    data class RemoveEpisodeFromQueue(val episodeId: Long) : PodcastDetailsUIEvent

    data object ErrorPlayingStatusConsumed : PodcastDetailsUIEvent
}
