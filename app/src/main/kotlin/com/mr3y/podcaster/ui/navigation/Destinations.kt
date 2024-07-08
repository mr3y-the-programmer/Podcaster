package com.mr3y.podcaster.ui.navigation

import com.kiwi.navigationcompose.typed.Destination
import kotlinx.serialization.Serializable

sealed interface Destinations : Destination {

    @Serializable
    data object SubscriptionsGraph : Destinations

    @Serializable
    data object ExploreGraph : Destinations

    @Serializable
    data object LibraryGraph : Destinations

    @Serializable
    data object SettingsGraph : Destinations

    @Serializable
    data object Subscriptions : Destinations

    @Serializable
    data object Explore : Destinations

    @Serializable
    sealed class PodcastDetails(val podcastId: Long) : Destinations

    @Serializable
    data class PodcastDetailsSubscriptionsGraph(val id: Long) : PodcastDetails(id)

    @Serializable
    data class PodcastDetailsExploreGraph(val id: Long) : PodcastDetails(id)

    @Serializable
    sealed class EpisodeDetails(val episodeId: Long, val podcastArtworkUrl: String) : Destinations

    @Serializable
    data class EpisodeDetailsSubscriptionsGraph(val id: Long, val artworkUrl: String) : EpisodeDetails(id, artworkUrl)

    @Serializable
    data class EpisodeDetailsExploreGraph(val id: Long, val artworkUrl: String) : EpisodeDetails(id, artworkUrl)

    @Serializable
    data class EpisodeDetailsLibraryGraph(val id: Long, val artworkUrl: String) : EpisodeDetails(id, artworkUrl)

    @Serializable
    data object Library : Destinations

    @Serializable
    data object Settings : Destinations

    @Serializable
    data object Downloads : Destinations

    @Serializable
    data object Licenses : Destinations

    @Serializable
    data object ImportExport : Destinations
}
