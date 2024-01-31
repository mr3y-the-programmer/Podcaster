package com.mr3y.podcaster.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PodcastEpisodeFeed(
    val id: Long,
    val guid: String,
    val title: String,
    val description: String,
    @SerialName("link")
    val episodeUrl: String,
    val datePublished: Long,
    @SerialName("datePublishedPretty")
    val datePublishedFormatted: String,
    @SerialName("duration")
    val durationInSec: Int? = null,
    @SerialName("episode")
    val episodeNum: Int? = null,
    @SerialName("image")
    val artworkUrl: String,
    val enclosureUrl: String,
    @SerialName("enclosureLength")
    val enclosureSizeInBytes: Long,
    @SerialName("feedId")
    val podcastId: Long,
    @SerialName("feedTitle")
    val podcastTitle: String? = null, // will exist if we are fetching the feed for a single network episode
)

@Serializable
data class NetworkEpisode(
    val status: Boolean,
    @SerialName("episode")
    val episodeFeed: PodcastEpisodeFeed,
)

@Serializable
data class NetworkEpisodes(
    val status: Boolean,
    @SerialName("items")
    val episodesFeed: List<PodcastEpisodeFeed>,
    val count: Long,
)
