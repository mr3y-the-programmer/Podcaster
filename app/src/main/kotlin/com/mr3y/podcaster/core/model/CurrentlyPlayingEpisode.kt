package com.mr3y.podcaster.core.model

data class CurrentlyPlayingEpisode(
    val episode: Episode,
    val playingStatus: PlayingStatus
)

enum class PlayingStatus {
    Loading,
    Playing,
    Paused,
    Error
}
