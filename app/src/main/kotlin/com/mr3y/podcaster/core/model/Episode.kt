package com.mr3y.podcaster.core.model

data class Episode(
    val id: Long,
    val podcastId: Long,
    val guid: String,
    val title: String,
    val description: String,
    val episodeUrl: String,
    val datePublishedTimestamp: Long,
    val datePublishedFormatted: String,
    val durationInSec: Int? = null,
    val episodeNum: Int? = null,
    val artworkUrl: String,
    val enclosureUrl: String,
    val enclosureSizeInBytes: Long,
    val podcastTitle: String? = null,
    val isDownloaded: Boolean = false,
    val isCompleted: Boolean = false,
    val progressInSec: Int? = null
)
