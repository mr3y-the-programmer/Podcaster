package com.mr3y.podcaster.core.model

data class EpisodeDownloadMetadata(
    val episodeId: Long,
    val downloadStatus: EpisodeDownloadStatus = EpisodeDownloadStatus.NotDownloaded,
    val downloadProgress: Float = 0f
)

enum class EpisodeDownloadStatus {
    NotDownloaded,
    Queued,
    Downloading,
    Paused,
    Downloaded
}
