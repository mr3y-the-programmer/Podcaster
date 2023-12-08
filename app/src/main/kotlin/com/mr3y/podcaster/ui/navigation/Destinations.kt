package com.mr3y.podcaster.ui.navigation

import com.kiwi.navigationcompose.typed.Destination
import kotlinx.serialization.Serializable

sealed interface Destinations : Destination {

    @Serializable
    data object Subscriptions : Destinations

    @Serializable
    data object Explore : Destinations

    @Serializable
    data class PodcastDetails(val podcastId: Long) : Destinations

    @Serializable
    data class EpisodeDetails(val episodeId: Long) : Destinations

    @Serializable
    data object Settings : Destinations

    @Serializable
    data object Downloads : Destinations
}
