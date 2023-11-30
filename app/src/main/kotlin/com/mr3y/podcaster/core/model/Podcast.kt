package com.mr3y.podcaster.core.model

data class Podcast(
    val id: Long,
    val guid: String,
    val title: String,
    val description: String,
    val podcastUrl: String,
    val website: String,
    val artworkUrl: String,
    val author: String,
    val owner: String,
    val languageCode: String,
    val episodeCount: Int,
    val genres: List<Genre>
)

data class Genre(
    val id: Int,
    val label: String
)
