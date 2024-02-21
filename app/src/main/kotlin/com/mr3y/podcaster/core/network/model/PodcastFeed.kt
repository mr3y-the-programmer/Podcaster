package com.mr3y.podcaster.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PodcastFeed(
    val id: Long,
    @SerialName("podcastGuid")
    val guid: String,
    val title: String,
    val description: String,
    @SerialName("url")
    val podcastUrl: String,
    @SerialName("link")
    val website: String,
    @SerialName("artwork")
    val artworkUrl: String,
    val author: String,
    @SerialName("ownerName")
    val owner: String,
    @SerialName("language")
    val languageCode: String,
    val episodeCount: Int,
    @SerialName("categories")
    val genres: Map<Int, String>?,
)

@Serializable
data class NetworkPodcast(
    val status: Boolean,
    val feed: PodcastFeed,
)

@Serializable
data class NetworkPodcasts(
    val status: Boolean,
    val feeds: List<PodcastFeed>,
    val count: Long,
)
