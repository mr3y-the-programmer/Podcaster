package com.mr3y.podcaster.core.model

import androidx.annotation.FloatRange

data class EpisodeDownloadMetadata(
    val episodeId: Long,
    val downloadStatus: EpisodeDownloadStatus = EpisodeDownloadStatus.NotDownloaded,
    @FloatRange(from = 0.0, to = 1.0) val downloadProgress: Float = 0f,
)

enum class EpisodeDownloadStatus {
    NotDownloaded,
    Queued,
    Downloading,
    Paused,
    Downloaded,
}
