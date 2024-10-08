package com.mr3y.podcaster.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Destinations {

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
    data class PodcastDetailsSubscriptionsGraph(val id: Long) : Destinations

    @Serializable
    data class PodcastDetailsExploreGraph(val id: Long) : Destinations

    @Serializable
    data class EpisodeDetailsSubscriptionsGraph(val id: Long, val artworkUrl: String) : Destinations

    @Serializable
    data class EpisodeDetailsExploreGraph(val id: Long, val artworkUrl: String) : Destinations

    @Serializable
    data class EpisodeDetailsLibraryGraph(val id: Long, val artworkUrl: String) : Destinations

    @Serializable
    data object Library : Destinations

    @Serializable
    data object Settings : Destinations

    @Serializable
    data object Downloads : Destinations

    @Serializable
    data object Favorites : Destinations

    @Serializable
    data object Licenses : Destinations

    @Serializable
    data object ImportExport : Destinations
}
