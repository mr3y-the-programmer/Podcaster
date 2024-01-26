package com.mr3y.podcaster.core.model

data class EpisodeWithDownloadMetadata(
    val episode: Episode,
    val downloadMetadata: EpisodeDownloadMetadata
)
